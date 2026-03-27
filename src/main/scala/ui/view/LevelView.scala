package ui.view

import logic.ExpandMode.NonExpanding
import logic.OverlayMode.{Opaque, Transparent}
import logic.RotationDirection.{CCW, CW}
import logic.{ExpandMode, OverlayMode, RotationDirection}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, ComboBox, Label, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.text.Font
import ui.ButtonCell
import ui.dialog.SaveLevelDialog
import ui.view.LevelEditState.{AddCentralSymmetry, AddNewIsometry, AddReflection, AddRotation, AddTranslation, CentralSymmetry, ChooseOperationType, ClearRectangle, Reflection, Rotation, Translation}
import utilities.style.{ButtonStyles, Images}
import scalafx.beans.property.ObjectProperty
import scalafx.Includes.*
import ui.view.RotationCentralSymmetrySelectionMode.Pivot
import ui.view.TranslationSelectionMode.{Original, Picture}

enum LevelEditState:
  case ChooseOperationType, DefaultBasicOperations, Expand, Remove, ClearRectangle, DefaultIsometry, Rotation, Reflection, AddNewIsometry, CentralSymmetry, Translation, AddRotation, AddReflection, AddTranslation, AddCentralSymmetry, ApplySavedIsometry

enum RotationCentralSymmetrySelectionMode:
  case Rectangle, Pivot

enum ReflectionSelectionMode:
  case Rectangle, Row, Column, DiagonalMain, DiagonalSecondary

enum TranslationSelectionMode:
  case Original, Picture

enum NewIsometrySelectionMode:
  case Rotation, Reflection, Translation, CentralSymmetry


case class Rectangle(startRow: Int, startCol: Int, endRow: Int, endCol:Int)

class LevelView(
                 getCellView: (Int, Int) => LevelCellView,
                 onLeft: (Int, Int) => Unit,
                 onAddRowFirst: () => Unit,
                 onAddRowLast: () => Unit,
                 onAddColFirst: () => Unit,
                 onAddColLast: () => Unit,
                 onRemoveRowFirst: () => Unit,
                 onRemoveRowLast: () => Unit,
                 onRemoveColFirst: () => Unit,
                 onRemoveColLast: () => Unit,
                 onClearRectangle: (Int, Int, Int, Int) => Unit,
                 onSave: String => Unit,
                 getRows: () => Int,
                 getCols: () => Int,
                 isValid: () => Boolean,
                 onApplyRotation: (Int, Int, Int, Int, ExpandMode, OverlayMode, Int, Int, RotationDirection, Boolean) => Unit,
                 onApplyReflection: (Int, Int, Int, Int, Option[Int], Option[Int], Option[(Int, Int)], Option[(Int, Int)], ExpandMode, OverlayMode, Boolean) => Unit,
                 onApplyCentralSymmetry: (Int, Int, Int, Int, ExpandMode, OverlayMode, Int, Int, Boolean) => Unit,
                 onApplyTranslation: (Int, Int, Int, Int, Int, Int, ExpandMode, OverlayMode, Boolean) => Unit,
                 onAddRotation: (Int, Int, RotationDirection) => Unit,
                 onAddReflection: (Option[Int], Option[Int], Option[(Int, Int)], Option[(Int, Int)]) => Unit,
                 onAddCentralSymmetry: (Int, Int) => Unit,
                 onAddTranslation: (Int, Int) => Unit,
                 isometryStepNames: () => List[String],
                 onSaveCustomIsometry: String => Unit,
                 savedIsometryNames: () => List[String],
                 onApplySavedIsometry: (String, Int, Int, Int, Int, ExpandMode, OverlayMode, Boolean) => Unit,
                 resetSelectedIsometry: () => Unit,
                 onResize: () => Unit,
                 onBack: () => Unit
               ) {


  private var state = ChooseOperationType
  private val rotationCentralSymmetrySelectionMode = ObjectProperty[RotationCentralSymmetrySelectionMode](RotationCentralSymmetrySelectionMode.Rectangle)
  private val reflectionSelectionMode = ObjectProperty[ReflectionSelectionMode](ReflectionSelectionMode.Rectangle)
  private val translationSelectionMode = ObjectProperty[TranslationSelectionMode](Original)
  private var rectangle: Option[Rectangle] = None
  private var row: Option[Int] = None
  private var column: Option[Int] = None
  private var diagonalMain: Option[(Int, Int)] = None
  private var diagonalSecondary: Option[(Int, Int)] = None
  private val rotationDirection = ObjectProperty[RotationDirection](CW)
  private var rotationPivot: Option[(Int, Int)] = None
  private val expandMode = ObjectProperty[ExpandMode](ExpandMode.NonExpanding)
  private val overlayMode = ObjectProperty[OverlayMode](OverlayMode.Transparent)
  private val quasiInverse = ObjectProperty[Boolean](false)
  private var original: Option[Rectangle] = None
  private var pictureStart: Option[(Int, Int)] = None
  private var translationShiftRows: Option[Int] = None
  private var translationShiftCols: Option[Int] = None
  private var selectedSavedIsometryName: Option[String] = None
  private def selectedStyle: String = ButtonStyles.ButtonMouseEntered
  private def unselectedStyle: String = ButtonStyles.ButtonClassic
  
  private val sidePanel: VBox = new VBox {
    spacing = 15
    padding = Insets(20)
    alignment = Pos.TopCenter
  }

  private def canApplyRotation: Boolean =
    rectangle.isDefined && rotationPivot.isDefined && rotationDirection.value != null
  private def canApplyRotationAdd: Boolean =
    rotationPivot.isDefined && rotationDirection.value != null

  private def canApplyCentralSymmetry: Boolean =
    rectangle.isDefined && rotationPivot.isDefined

  private def canApplyCentralSymmetryAdd: Boolean =
      rotationPivot.isDefined

  private def canApplyTranslation: Boolean =
    original match
      case None =>
        false
      case Some(Rectangle(startRow, startCol, endRow, endCol)) =>
        pictureStart match
          case None =>
            false
          case Some(pictureStartRow, pictureStartCol) =>
            val startR = Math.min(startRow, endRow)
            val startC = Math.min(startCol, endCol)
            ((startR - pictureStartRow)  % 2 == 0 && (startC - pictureStartCol) % 2 == 0)
      case None =>
        false
  private def canApplyTranslationAdd: Boolean =
    translationShiftRows.isDefined || translationShiftCols.isDefined


  private def canApplyReflection: Boolean =
    rectangle.isDefined && canApplyReflectionAdd

  private def canApplyReflectionAdd: Boolean =
      reflectionSelectionMode.value match
        case ReflectionSelectionMode.Row => row.isDefined
        case ReflectionSelectionMode.Column => column.isDefined
        case ReflectionSelectionMode.DiagonalMain => diagonalMain.isDefined
        case ReflectionSelectionMode.DiagonalSecondary => diagonalSecondary.isDefined
        case ReflectionSelectionMode.Rectangle => row.isDefined || column.isDefined || diagonalMain.isDefined || diagonalSecondary.isDefined

  private def showSelectedRectangle(buttons: Array[Array[ButtonCell]]): Unit = {
    rectangle match
      case None =>
      case Some(Rectangle(startRow, startCol, endRow, endCol)) if !(startRow == endRow && startCol == endCol) =>
        val startR = Math.min(startRow, endRow)
        val endR = Math.max(startRow, endRow)
        val startC = Math.min(startCol, endCol)
        val endC = Math.max(startCol, endCol)
        for {
          row <- startR to endR
          col <- startC to endC
        } {
          buttons(row)(col).style = SelectedRectangle().style
          if state == ClearRectangle then
            buttons(row)(col).graphic = null
        }
      case Some(Rectangle(startRow, startCol, endRow, endCol)) if startRow == endRow && startCol == endCol =>
        buttons(startRow)(startCol).style = SelectedRectangle().style
        if state == ClearRectangle then
          buttons(startRow)(startCol).graphic = null
      case Some(_) =>
  }
  private def showSelectedOriginalAndPicture(buttons: Array[Array[ButtonCell]]): Unit = {
    original match
      case None =>
        pictureStart match
          case None =>
          case Some(pictureStartRow, pictureStartCol) =>
            buttons(pictureStartRow)(pictureStartCol).style = SelectedPicture().style
      case Some(Rectangle(startRow, startCol, endRow, endCol)) if !(startRow == endRow && startCol == endCol) =>
        val startR = Math.min(startRow, endRow)
        val endR = Math.max(startRow, endRow)
        val startC = Math.min(startCol, endCol)
        val endC = Math.max(startCol, endCol)
        for {
          row <- startR to endR
          col <- startC to endC
        } {
          buttons(row)(col).style = SelectedRectangle().style
        }
        pictureStart match
          case None =>
          case Some(row, col) =>
            val rowDiff = row - startR
            val colDiff = col - startC
            val rows = getRows()
            val cols = getCols()
            for {
              row <- startR to endR
              col <- startC to endC
            } {
              val pictureRow = row + rowDiff
              val pictureCol = col + colDiff
              if 0 <= pictureRow && pictureRow < rows && 0 <= pictureCol  && pictureCol < cols then
                buttons(pictureRow)(pictureCol).style = SelectedPicture().style
            }
      case Some(Rectangle(startRow, startCol, endRow, endCol)) if startRow == endRow && startCol == endCol =>
        buttons(startRow)(startCol).style = SelectedRectangle().style
        pictureStart match
          case None =>
          case Some(row, col) =>
            buttons(row)(col).style = SelectedPicture().style
      case Some(_) =>
  }
  private def setState(newState: LevelEditState): Unit = {
    state = newState
    updateSidePanel()
  }
  private def operationButton(text: String)(action: => Unit): Button =
    new Button(text) {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      onAction = _ => action
    }

  val saveButton: Button = operationButton("Сачувај") {
    val dialog = new SaveLevelDialog(onSave)
    dialog.show()
    resetAllStates()
  }

  private def chooseOperationTypeSidePanel(): Seq[javafx.scene.Node] = Seq(
    new Label("Изаберите тип операција") {
      font = Font.font(18)
    },
    operationButton("Обичне операције") {
      setState(LevelEditState.DefaultBasicOperations)
    },
    operationButton("Изометрије") {
      setState(LevelEditState.DefaultIsometry)
    },
    operationButton("Назад") {
      onBack()
    },
    saveButton
  )
  private def defaultBasicOperationsSidePanel(): Seq[javafx.scene.Node] = Seq(
    new Label("Прављење новог нивоа") {
      font = Font.font(18)
    },
    operationButton("Додавање редова/колона") {
      setState(LevelEditState.Expand)
    },
    operationButton("Брисање редова/колона") {
      setState(LevelEditState.Remove)
    },
    operationButton("Очисти део табле") {
      setState(LevelEditState.ClearRectangle)
    },
    operationButton("Назад") {
      setState(LevelEditState.ChooseOperationType)
    },
    saveButton
  )
  private val rectangleModeButton: Button = new Button {
    graphic = new ImageView(Images.RectangleSelect) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if (((state == Rotation || state == CentralSymmetry) && rotationCentralSymmetrySelectionMode.value == RotationCentralSymmetrySelectionMode.Rectangle) ||
        (state == Reflection && reflectionSelectionMode.value == ReflectionSelectionMode.Rectangle))
        selectedStyle
      else unselectedStyle

    onAction = _ => {
      if state == Rotation || state == CentralSymmetry then
        rotationCentralSymmetrySelectionMode.value  = RotationCentralSymmetrySelectionMode.Rectangle
      else if state == Reflection then
        reflectionSelectionMode.value = ReflectionSelectionMode.Rectangle
      updateSidePanel()
    }
  }
  private val rowModeButton: Button = new Button {
    graphic = new ImageView(Images.RowSelect) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if reflectionSelectionMode.value == ReflectionSelectionMode.Row then selectedStyle
      else unselectedStyle

    onAction = _ => {
      reflectionSelectionMode.value = ReflectionSelectionMode.Row
      updateSidePanel()
    }
  }
  
  private val columnModeButton: Button = new Button {
    graphic = new ImageView(Images.ColumnSelect) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if reflectionSelectionMode == ReflectionSelectionMode.Column then selectedStyle
      else unselectedStyle

    onAction = _ => {
      reflectionSelectionMode.value = ReflectionSelectionMode.Column
      updateSidePanel()
    }
  }
  private val diagonal1ModeButton: Button = new Button {
    graphic = new ImageView(Images.Diagonal1Select) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if reflectionSelectionMode.value == ReflectionSelectionMode.DiagonalMain then selectedStyle
      else unselectedStyle

    onAction = _ => {
      reflectionSelectionMode.value = ReflectionSelectionMode.DiagonalMain
      updateSidePanel()
    }
  }
  private val diagonal2ModeButton: Button = new Button {
    graphic = new ImageView(Images.Diagonal2Select) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if reflectionSelectionMode.value == ReflectionSelectionMode.DiagonalSecondary then selectedStyle
      else unselectedStyle

    onAction = _ => {
      reflectionSelectionMode.value = ReflectionSelectionMode.DiagonalSecondary
      updateSidePanel()
    }
  }
  private val pivotModeButton = new Button {
    graphic = new ImageView(Images.PivotSelect) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if rotationCentralSymmetrySelectionMode.value == RotationCentralSymmetrySelectionMode.Pivot then selectedStyle
      else unselectedStyle

    onAction = _ => {
      rotationCentralSymmetrySelectionMode.value = RotationCentralSymmetrySelectionMode.Pivot
      updateSidePanel()
    }
  }
  rotationCentralSymmetrySelectionMode.onChange { (_, _, newValue) =>
    rectangleModeButton.style =
      if newValue == RotationCentralSymmetrySelectionMode.Rectangle then selectedStyle
      else unselectedStyle

    pivotModeButton.style =
      if newValue == Pivot then selectedStyle
      else unselectedStyle
  }

  reflectionSelectionMode.onChange { (_, _, newValue) =>
    rectangleModeButton.style =
      if newValue == ReflectionSelectionMode.Rectangle then selectedStyle
      else unselectedStyle

    rowModeButton.style =
      if newValue == ReflectionSelectionMode.Row then selectedStyle
      else unselectedStyle
    columnModeButton.style =
      if newValue == ReflectionSelectionMode.Column then selectedStyle
      else unselectedStyle
    diagonal1ModeButton.style =
      if newValue == ReflectionSelectionMode.DiagonalMain then selectedStyle
      else unselectedStyle
    diagonal2ModeButton.style =
      if newValue == ReflectionSelectionMode.DiagonalSecondary then selectedStyle
      else unselectedStyle
  }
  private val cwButton = new Button {
    graphic = new ImageView(Images.RotateCW) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if rotationDirection.value == RotationDirection.CW then selectedStyle
      else unselectedStyle

    onAction = _ => {
      rotationDirection.value = RotationDirection.CW
      updateSidePanel()
    }
  }

  private val ccwButton = new Button {
    graphic = new ImageView(Images.RotateCCW) {
      fitWidth = 20
      fitHeight = 20
      preserveRatio = true
    }
    style =
      if rotationDirection == RotationDirection.CCW then selectedStyle
      else unselectedStyle

    onAction = _ => {
      rotationDirection.value = RotationDirection.CCW
      updateSidePanel()
    }
  }
  rotationDirection.onChange { (_, _, newValue) =>
    cwButton.style =
      if newValue == CW then selectedStyle
      else unselectedStyle

    ccwButton.style =
      if newValue == CCW then selectedStyle
      else unselectedStyle
  }
  private val expandingYesButton = new Button("Да") {
    minWidth = 60
    style =
      if expandMode.value  == ExpandMode.Expanding then selectedStyle
      else unselectedStyle

    onAction = _ => {
      expandMode.value = ExpandMode.Expanding
    }
  }

  private val expandingNoButton = new Button("Не") {
    minWidth = 60
    style =
      if expandMode.value == ExpandMode.NonExpanding then selectedStyle
      else unselectedStyle

    onAction = _ => {
      expandMode.value = ExpandMode.NonExpanding
    }
  }
  expandMode.onChange { (_, _, newValue) =>
    expandingYesButton.style =
      if newValue == ExpandMode.Expanding then selectedStyle
      else unselectedStyle

    expandingNoButton.style =
      if newValue == ExpandMode.NonExpanding then selectedStyle
      else unselectedStyle
  }
  private val transparentYesButton = new Button("Да") {
    minWidth = 60
    style =
      if overlayMode.value == OverlayMode.Transparent then selectedStyle
      else unselectedStyle

    onAction = _ => {
      overlayMode.value = OverlayMode.Transparent
      updateSidePanel()
    }
  }

  private val transparentNoButton = new Button("Не") {
    minWidth = 60
    style =
      if overlayMode.value == OverlayMode.Opaque then selectedStyle
      else unselectedStyle

    onAction = _ => {
      overlayMode.value = OverlayMode.Opaque
      updateSidePanel()
    }
  }
  private val quasiInverseYesButton = new Button("Да") {
    minWidth = 60
    style =
      if quasiInverse.value then selectedStyle
      else unselectedStyle

    onAction = _ => {
      quasiInverse.value = true
      updateSidePanel()
    }
  }

  private val quasiInverseNoButton = new Button("Не") {
    minWidth = 60
    style =
      if !quasiInverse.value then selectedStyle
      else unselectedStyle

    onAction = _ => {
      quasiInverse.value = false
      updateSidePanel()
    }
  }
  overlayMode.onChange { (_, _, newValue) =>
    transparentYesButton.style =
      if newValue == Transparent then selectedStyle
      else unselectedStyle

    transparentNoButton.style =
      if newValue == Opaque then selectedStyle
      else unselectedStyle
  }
  quasiInverse.onChange { (_, _, newValue) =>
      quasiInverseYesButton.style =
        if newValue then selectedStyle
        else unselectedStyle
  
      quasiInverseNoButton.style =
        if !newValue then selectedStyle
        else unselectedStyle
    }

  private def rotationSidePanel(): Seq[javafx.scene.Node] = {
    val applyRotationButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyRotation

      onAction = _ => {

        onApplyRotation(rectangle.get.startRow, rectangle.get.startCol, rectangle.get.endRow, rectangle.get.endCol, expandMode.value, overlayMode.value, rotationPivot.get._1, rotationPivot.get._2, rotationDirection.value, quasiInverse.value)
        resetAllStates()
      }
    }
    Seq(
      new Label("Ротација") {
        font = Font.font(18)
      },

      new Label("Изаберите режим:") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(rectangleModeButton, pivotModeButton)
      },

      new Label("Смер ротације") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 15
        alignment = Pos.Center
        children = Seq(cwButton, ccwButton)
      },

      new Label("Проширива") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(expandingYesButton, expandingNoButton)
      },

      new Label("Транспарентна") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(transparentYesButton, transparentNoButton)
      },
      new Label("Квази инверз") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(quasiInverseYesButton, quasiInverseNoButton)
      },
      applyRotationButton,

      operationButton("Назад") {
        setState(LevelEditState.DefaultIsometry)
        resetAllStates()
      }
    )
  }

  private def rotationSidePanelAdd(): Seq[javafx.scene.Node] = {
    val addRotationButton = new Button("Додај") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyRotationAdd

      onAction = _ => {
        onAddRotation(rotationPivot.get._1, rotationPivot.get._2, rotationDirection.value)
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    }
    Seq(
      new Label("Ротација") {
        font = Font.font(18)
      },

      new Label("Изаберите пивот кликом на поље:") {
        font = Font.font(14)
      },

      new Label("Смер ротације") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 15
        alignment = Pos.Center
        children = Seq(cwButton, ccwButton)
      },

      addRotationButton,

      operationButton("Назад") {
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    )
  }
  private def centralSymmetrySidePanel(): Seq[javafx.scene.Node] = {
    val applyCentralSymmetryButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyCentralSymmetry

      onAction = _ => {
        onApplyCentralSymmetry(rectangle.get.startRow, rectangle.get.startCol, rectangle.get.endRow, rectangle.get.endCol, expandMode.value, overlayMode.value, rotationPivot.get._1, rotationPivot.get._2, quasiInverse.value)
        resetAllStates()
      }
    }
    Seq(
      new Label("Централна симетрија") {
        font = Font.font(18)
      },

      new Label("Изаберите режим:") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(rectangleModeButton, pivotModeButton)
      },

      new Label("Проширива") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(expandingYesButton, expandingNoButton)
      },

      new Label("Транспарентна") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(transparentYesButton, transparentNoButton)
      },
      new Label("Квази инверз") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(quasiInverseYesButton, quasiInverseNoButton)
      },
      applyCentralSymmetryButton,

      operationButton("Назад") {
        setState(LevelEditState.DefaultIsometry)
        resetAllStates()
      }
    )
  }
  private def centralSymmetrySidePanelAdd(): Seq[javafx.scene.Node] = {
    val addCentralSymmetryButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyCentralSymmetryAdd

      onAction = _ => {
        onAddCentralSymmetry(rotationPivot.get._1, rotationPivot.get._2)
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    }
    Seq(
      new Label("Централна симетрија") {
        font = Font.font(18)
      },

      new Label("Изаберите пивот кликом на поље:") {
        font = Font.font(14)
      },

      addCentralSymmetryButton,

      operationButton("Назад") {
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    )
  }

  private def translationSidePanel(): Seq[javafx.scene.Node] = {
    val applyTranslationButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyTranslation

      onAction = _ => {
        onApplyTranslation(original.get.startRow, original.get.startCol, original.get.endRow, original.get.endCol, pictureStart.get._1, pictureStart.get._2, expandMode.value, overlayMode.value, quasiInverse.value)
        resetSelectedIsometry()
        resetAllStates()
      }
    }
    val originalRectangleModeButton: Button = new Button {
      graphic = new ImageView(Images.RectangleSelect) {
        fitWidth = 20
        fitHeight = 20
        preserveRatio = true
      }
      style =
        if translationSelectionMode.value == Original then
          selectedStyle
        else unselectedStyle

      onAction = _ => {
        translationSelectionMode.value = Original
        updateSidePanel()
      }
    }
    val pictureRectangleModeButton: Button = new Button {
      graphic = new ImageView(Images.RectangleCopy) {
        fitWidth = 20
        fitHeight = 20
        preserveRatio = true
      }
      style =
        if translationSelectionMode.value == Picture then
          selectedStyle
        else unselectedStyle

      onAction = _ => {
        translationSelectionMode.value = Picture
        updateSidePanel()
      }
    }
    Seq(
      new Label("Транслација") {
        font = Font.font(18)
      },

      new Label("Изаберите режим:") {
        font = Font.font(14)
      },
      new Label("Дозвољено је померити правоугаони сектор за паран број врста/колона") {
        font = Font.font(10)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(originalRectangleModeButton, pictureRectangleModeButton)
      },

      new Label("Проширива") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(expandingYesButton, expandingNoButton)
      },

      new Label("Транспарентна") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(transparentYesButton, transparentNoButton)
      },
      new Label("Квази инверз") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(quasiInverseYesButton, quasiInverseNoButton)
      },
      applyTranslationButton,

      operationButton("Назад") {
        setState(LevelEditState.DefaultIsometry)
        resetAllStates()
      }
    )
  }

  private def translationSidePanelAdd(): Seq[javafx.scene.Node] = {

//    val rowsButton = new Button("Редови") {
//      minWidth = 100
//      style =
//        None
//
////      onAction = _ => {
////        translationType = TranslationType.Vertical
////        updateSidePanel()
////      }
//    }

//    val colsButton = new Button("Колоне") {
//      minWidth = 100
//      style =
//        None
//
//      onAction = _ => {
//        translationType = TranslationType.Horizontal
//        updateSidePanel()
//      }
//    }

    val addTranslationButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = translationShiftRows.isEmpty && translationShiftCols.isEmpty

      onAction = _ => {
        onAddTranslation(translationShiftRows.get, translationShiftCols.get)
        setState(LevelEditState.AddNewIsometry)
        updateSidePanel()
      }
    }

    val shiftRowsTextField = new TextField {
      promptText = "Редови"
      prefWidth = 160
      text = translationShiftRows.map(_.toString).getOrElse("")

      text.onChange { (_, _, newValue) =>
        val filtered = newValue.zipWithIndex
          .filter { case (ch, i) => ch.isDigit || (ch == '-' && i == 0) }
          .map(_._1)
          .mkString

        if filtered != newValue then
          text.value = filtered
        else
          translationShiftRows =
            if filtered.nonEmpty then Some(filtered.toInt)
            else None

        addTranslationButton.disable = translationShiftRows.isEmpty && translationShiftCols.isEmpty
      }
    }
    val shiftColsTextField = new TextField {
      promptText = "Колоне"
      prefWidth = 160
      text = translationShiftCols.map(_.toString).getOrElse("")

      text.onChange { (_, _, newValue) =>
        val filtered = newValue.zipWithIndex
          .filter { case (ch, i) => ch.isDigit || (ch == '-' && i == 0) }
          .map(_._1)
          .mkString

        if filtered != newValue then
          text.value = filtered
        else
          translationShiftCols =
            if filtered.nonEmpty then Some(filtered.toInt)
            else None

        addTranslationButton.disable = translationShiftRows.isEmpty && translationShiftCols.isEmpty
      }
    }

    Seq(
      new Label("Транслација") {
        font = Font.font(18)
      },
      shiftRowsTextField,
      shiftColsTextField,
      addTranslationButton,
      operationButton("Назад") {
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    )
  }
  private def resetAllStates(): Unit = {
    rectangle = None
    row = None
    column = None
    diagonalMain = None
    diagonalSecondary = None
    rotationPivot = None
    expandMode.value = NonExpanding
    overlayMode.value = Transparent
    quasiInverse.value = false
    original = None
    pictureStart = None
    newIsometryNameField.text = ""
    //translationType = TranslationType.Horizontal
    translationShiftRows = None
    translationShiftCols = None
    //resetSelectedIsometry()
    refreshUI()
  }
  private def reflectionSidePanel(): Seq[javafx.scene.Node] = {
    val applyReflectionButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyReflection

      onAction = _ => {
        onApplyReflection(rectangle.get.startRow, rectangle.get.startCol, rectangle.get.endRow, rectangle.get.endCol, row, column, diagonalMain, diagonalSecondary, expandMode.value, overlayMode.value, quasiInverse.value)
        resetAllStates()
      }
    }
    Seq(
      new Label("Осна рефлексија") {
        font = Font.font(18)
      },

      new Label("Изаберите режим:") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(rectangleModeButton, rowModeButton, columnModeButton, diagonal1ModeButton, diagonal2ModeButton)
      },

      new Label("Проширива") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(expandingYesButton, expandingNoButton)
      },

      new Label("Транспарентна") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(transparentYesButton, transparentNoButton)
      },
      new Label("Квази инверз") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(quasiInverseYesButton, quasiInverseNoButton)
      },
      applyReflectionButton,

      operationButton("Назад") {
        setState(LevelEditState.DefaultIsometry)
        resetAllStates()
      }
    )
  }

  private def reflectionSidePanelAdd(): Seq[javafx.scene.Node] = {
    val addReflectionButton = new Button("Додај") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyReflectionAdd

      onAction = _ => {
        onAddReflection(row, column, diagonalMain, diagonalSecondary)
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    }
    Seq(
      new Label("Осна рефлексија") {
        font = Font.font(18)
      },

      new Label("Изаберите режим:") {
        font = Font.font(14)
      },
      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(rowModeButton, columnModeButton, diagonal1ModeButton, diagonal2ModeButton)
      },
      addReflectionButton,
      operationButton("Назад") {
        setState(LevelEditState.AddNewIsometry)
        resetAllStates()
      }
    )
  }

  private def isometrySidePanel(): Seq[javafx.scene.Node] = {
    val savedIsometryButtons =
      savedIsometryNames().map { name =>
        operationButton(name) {
          selectedSavedIsometryName = Some(name)
          setState(LevelEditState.ApplySavedIsometry)
          updateSidePanel()
        }.delegate
      }

    Seq[javafx.scene.Node](
      new Label("Изометрије") {
        font = Font.font(18)
      },
      operationButton("Ротација") {
        setState(LevelEditState.Rotation)
      },
      operationButton("Рефлексија") {
        setState(LevelEditState.Reflection)
      },
      operationButton("Централна симетрија") {
        setState(LevelEditState.CentralSymmetry)
      },
      operationButton("Транслација") {
        setState(LevelEditState.Translation)
      }
    ) ++
      savedIsometryButtons ++
      Seq(
        operationButton("Направи нову изометрију") {
          setState(LevelEditState.AddNewIsometry)
        },
        operationButton("Назад") {
          setState(LevelEditState.ChooseOperationType)
        },
        saveButton
      )
  }

  private def applySavedIsometrySidePanel(): Seq[javafx.scene.Node] = {
    val applyButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = rectangle.isEmpty

      onAction = _ => {
        for {
          name <- selectedSavedIsometryName
          rect <- rectangle
        } {
          onApplySavedIsometry(selectedSavedIsometryName.get, rectangle.get.startRow, rectangle.get.startCol, rectangle.get.endRow, rectangle.get.endCol, expandMode.value, overlayMode.value, quasiInverse.value )
          rectangle = None
          expandMode.value = NonExpanding
          overlayMode.value = Transparent
          quasiInverse.value = false
          selectedSavedIsometryName = None
          refreshUI()
        }
      }
    }

    Seq(
      new Label(
        selectedSavedIsometryName match
          case Some(name) => name
          case None => "Сачувана изометрија"
      ) {
        font = Font.font(18)
      },

      new Label("Изаберите правоугаоник") {
        font = Font.font(14)
      },
      new Label("Проширива") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(expandingYesButton, expandingNoButton)
      },

      new Label("Транспарентна") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(transparentYesButton, transparentNoButton)
      },
      new Label("Квази инверз") {
        font = Font.font(14)
      },

      new HBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(quasiInverseYesButton, quasiInverseNoButton)
      },
      applyButton,
      operationButton("Назад") {
        selectedSavedIsometryName = None
        rectangle = None
        setState(LevelEditState.DefaultIsometry)
      }
      )
  }
  private def expandSidePanel(): Seq[javafx.scene.Node] = Seq(
    new Label("Проширивање") {
      font = Font.font(16)
    },
    operationButton("Додај ред на почетак") {
      onAddRowFirst()
      refreshUI()
    },
    operationButton("Додај ред на крај") {
      onAddRowLast()
      refreshUI()
    },
    operationButton("Додај колону на почетак") {
      onAddColFirst()
      refreshUI()
    },
    operationButton("Додај колону на крај") {
      onAddColLast()
      refreshUI()
    },
    operationButton("Назад") {
      setState(LevelEditState.DefaultBasicOperations)
    }
  )

  private def removeSidePanel(): Seq[javafx.scene.Node] = Seq(
    new Label("Брисање") {
      font = Font.font(16)
    },
    operationButton("Обриши први ред") {
      onRemoveRowFirst()
      refreshUI()
    },
    operationButton("Обриши последњи ред") {
      onRemoveRowLast()
      refreshUI()

    },
    operationButton("Обриши прву колону") {
      onRemoveColFirst()
      refreshUI()

    },
    operationButton("Обриши последњу колону") {
      onRemoveColLast()
      refreshUI()

    },
    operationButton("Назад") {
      setState(LevelEditState.DefaultBasicOperations)
    }
  )

  private def clearRectangleSidePanel(): Seq[javafx.scene.Node] = Seq(
    new Label("Чишћење правоугаоника\n\nПотребно је изабрати прво и последње поље") {
      font = Font.font(16)
    },
    operationButton("Потврди") {
      rectangle match
        case Some(Rectangle(startRow, startCol, endRow, endCol)) => onClearRectangle(startRow, startCol, endRow, endCol)
        case None =>
      rectangle = None
      refreshUI()
    },
    operationButton("Назад") {
      setState(LevelEditState.DefaultBasicOperations)
      rectangle = None
      refreshUI()
    }
  )

  private def updateSidePanel(): Unit = {
    val content = state match
      case LevelEditState.ChooseOperationType => chooseOperationTypeSidePanel()
      case LevelEditState.DefaultBasicOperations => defaultBasicOperationsSidePanel()
      case LevelEditState.Expand => expandSidePanel()
      case LevelEditState.Remove => removeSidePanel()
      case LevelEditState.ClearRectangle => clearRectangleSidePanel()
      case LevelEditState.DefaultIsometry => isometrySidePanel()
      case LevelEditState.Rotation => rotationSidePanel()
      case LevelEditState.Reflection => reflectionSidePanel()
      case LevelEditState.CentralSymmetry => centralSymmetrySidePanel()
      case LevelEditState.Translation => translationSidePanel()
      case LevelEditState.AddNewIsometry => addNewIsometrySidePanel()
      case AddRotation => rotationSidePanelAdd()
      case AddReflection => reflectionSidePanelAdd()
      case AddTranslation => translationSidePanelAdd()
      case AddCentralSymmetry => centralSymmetrySidePanelAdd()
      case LevelEditState.ApplySavedIsometry => applySavedIsometrySidePanel()
    sidePanel.children.setAll(content *)
    onResize()
  }

  updateSidePanel()
  val grid: GridPane = new GridPane

    
  private def onLeftClick(row:Int, col:Int): Unit = {
    if state != ClearRectangle && state != Rotation && state != Reflection && state != CentralSymmetry && state != Translation && state != AddNewIsometry && state != AddCentralSymmetry && state != AddTranslation && state != LevelEditState.AddRotation && state != AddReflection && state != LevelEditState.ApplySavedIsometry then
      onLeft(row, col)
      refreshUI()
    else if state == LevelEditState.ApplySavedIsometry || state == ClearRectangle || (state == Rotation || state == CentralSymmetry) && rotationCentralSymmetrySelectionMode.value == RotationCentralSymmetrySelectionMode.Rectangle || state == Reflection && reflectionSelectionMode.value == ReflectionSelectionMode.Rectangle then
      rectangle match
        case None =>
          rectangle = Some(Rectangle(row, col, row, col ))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) if startRow == endRow && startCol == endCol =>
          rectangle = Some(Rectangle(rectangle.get.startRow, rectangle.get.startCol, row, col))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) =>
          rectangle = Some(Rectangle(rectangle.get.endRow, rectangle.get.endCol, row, col))
      showCurrentSelection()
      updateSidePanel()
    else if ((state == Rotation || state == CentralSymmetry) && rotationCentralSymmetrySelectionMode.value == Pivot) || state == AddRotation || state == AddCentralSymmetry then
      rotationPivot = Some(row, col)
      showCurrentSelection()
      updateSidePanel()
    else if (state == Reflection || state == AddReflection) && reflectionSelectionMode.value == ReflectionSelectionMode.Row then
      this.row = Some(row)
      showReflectionSelectedRow()
    else if (state == Reflection || state == AddReflection) && reflectionSelectionMode.value == ReflectionSelectionMode.Column then
      this.column = Some(col)
      showReflectionSelectedColumn()
    else if (state == Reflection || state == AddReflection) && reflectionSelectionMode.value == ReflectionSelectionMode.DiagonalMain then
      this.diagonalMain = Some((row, col))
      showReflectionSelectedDiagonal1()
    else if (state == Reflection || state == AddReflection) && reflectionSelectionMode.value == ReflectionSelectionMode.DiagonalSecondary then
      this.diagonalSecondary = Some((row, col))
      showReflectionSelectedDiagonal2()
    else if state == Translation && translationSelectionMode.value == Original then
      original match
        case None =>
          original = Some(Rectangle(row, col, row, col ))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) if startRow == endRow && startCol == endCol =>
          original = Some(Rectangle(original.get.startRow, original.get.startCol, row, col))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) =>
          original = Some(Rectangle(original.get.endRow, original.get.endCol, row, col))
      showTranslationSelection()
      updateSidePanel()
    else if state == Translation && translationSelectionMode.value == Picture || state == AddTranslation then
      pictureStart = Some(row, col)
      showTranslationSelection()
      updateSidePanel()


  }

  private def showCurrentSelection(): Unit = {
    grid.children.clear()
    val rows = getRows()
    val cols = getCols()
    val buttons = setButtons()
    showSelectedRectangle(buttons)
    if rotationPivot.isDefined then
      buttons(rotationPivot.get._1)(rotationPivot.get._2).style = PivotCellView().style

  }

  def showTranslationSelection(): Unit = {{
    grid.children.clear()
    val rows = getRows()
    val cols = getCols()
    val buttons = setButtons()
    showSelectedOriginalAndPicture(buttons)
  }}
  private def showReflectionSelectedRow(): Unit = {
    grid.children.clear()
    val rows = getRows()
    val cols = getCols()
    val buttons = setButtons()
    showSelectedRectangle(buttons)
    for (c <- 0 until cols) {
      buttons(row.get)(c).style = ReflectionAxisCellView().style
    }
    column = None
    diagonalMain = None
    diagonalSecondary = None
    updateSidePanel()
  }
  private def showReflectionSelectedColumn(): Unit = {
    grid.children.clear()
    val rows = getRows()
    val cols = getCols()
    val buttons = setButtons()
    showSelectedRectangle(buttons)
    for (r <- 0 until rows) {
      buttons(r)(column.get).style = ReflectionAxisCellView().style
    }
    row = None
    diagonalMain = None
    diagonalSecondary = None
    updateSidePanel()
  }
  private def showReflectionSelectedDiagonal1(): Unit = {
    grid.children.clear()
    val rows = getRows()
    val cols = getCols()
    val buttons = setButtons()
    showSelectedRectangle(buttons)
    val diff = diagonalMain.get._1 - diagonalMain.get._2
    for (r <- 0 until rows) {
      val c = r - diff
      if c >= 0 && c < cols then
        buttons(r)(c).style = ReflectionAxisCellView().style
    }
    row = None
    column = None
    diagonalSecondary = None
    updateSidePanel()
  }
  private def showReflectionSelectedDiagonal2(): Unit = {
    grid.children.clear()
    val rows = getRows()
    val cols = getCols()
    val buttons = setButtons()
    showSelectedRectangle(buttons)
    val sum = diagonalSecondary.get._1 + diagonalSecondary.get._2
    for (r <- 0 until rows) {
      val c = sum - r
      if c >= 0 && c < cols then
        buttons(r)(c).style = ReflectionAxisCellView().style
    }
    row = None
    column = None
    diagonalMain = None
    updateSidePanel()
  }
  refreshUI()

  private def setButtons(): Array[Array[ButtonCell]] = {
    val rows = getRows()
    val cols = getCols()
    val buttons: Array[Array[ButtonCell]] =
      Array.ofDim(rows, cols)
    for (r <- 0 until getRows(); c <- 0 until getCols()) {
      val btn = new ButtonCell(r, c, onLeftClick, (r, c) => ())
      buttons(r)(c) = btn
      grid.add(btn, c, r)
    }
    for {
      row <- 0 until rows
      col <- 0 until cols
    } {
      updateButton(buttons(row)(col), row, col)
    }
    buttons
  }
  private def refreshUI(): Unit = {
    grid.children.clear()
    setButtons()
    saveButton.disable = !isValid()
    updateSidePanel()
  }

  private def updateButton(btn: Button, row: Int, col: Int): Unit = {
    val cellView: LevelCellView = getCellView(row, col)
    btn.style = cellView.style
    btn.graphic = cellView.graphic.orNull
  }

  val root: HBox = new HBox {
    spacing = 30
    padding = Insets(20)
    alignment = Pos.Center
    fillHeight = false
    children = Seq(grid, sidePanel)
  }
  private val newIsometryNameField = new TextField {
    promptText = "Унеси име изометрије"
  }

  private def canSaveNewIsometry: Boolean =
    newIsometryNameField.text.value.trim.nonEmpty



  private def newIsometryNodes(): Seq[javafx.scene.Node] =

    val names = isometryStepNames()
    if names.isEmpty then
      Seq(
        new Label("Нема изабраних корака.") {
          font = Font.font(12)
        }
      )
    else
      names.zipWithIndex.map { case (name, index) =>
        new Label(s"${index + 1}. ${name}") {
          font = Font.font(12)
        }
      }

  private def addNewIsometrySidePanel(): Seq[javafx.scene.Node] = {
    val addRotationButton = operationButton("Ротација") {
      setState(AddRotation)
      updateSidePanel()
    }

    val addReflectionButton = operationButton("Рефлексија") {
      setState(AddReflection)
      updateSidePanel()
    }

    val addTranslationButton = operationButton("Транслација") {
      setState(AddTranslation)
      updateSidePanel()
    }

    val addCentralSymmetryButton = operationButton("Централна симетрија") {
      setState(AddCentralSymmetry)
      updateSidePanel()
    }



    val saveNewIsometryButton = new Button("Сачувај изометрију") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canSaveNewIsometry

      onAction = _ => {
        onSaveCustomIsometry(newIsometryNameField.text.value.trim)
        resetAllStates()
        setState(LevelEditState.DefaultIsometry)
      }
    }

    newIsometryNameField.text.onChange { (_, _, _) =>
      saveNewIsometryButton.disable = !canSaveNewIsometry
    }

    Seq[javafx.scene.Node](
      new Label("Нова изометрија") {
        font = Font.font(18)
      },

      new Label("Додај корак:") {
        font = Font.font(14)
      },

      addRotationButton,
      addReflectionButton,
      addTranslationButton,
      addCentralSymmetryButton,


    ) ++
      newIsometryNodes() ++
      Seq(

        new Label("Име изометрије:") {
          font = Font.font(14)
        },

        newIsometryNameField,

        saveNewIsometryButton,

        operationButton("Назад") {
          resetAllStates()
          setState(LevelEditState.DefaultIsometry)
        }
      )
  }


}

