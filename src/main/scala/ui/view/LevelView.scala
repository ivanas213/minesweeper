package ui.view

import logic.ExpandMode.NonExpanding
import logic.OverlayMode.Transparent
import logic.{ExpandMode, OverlayMode, RotationDirection}
import model.Level
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.text.Font
import ui.ButtonCell
import ui.dialog.SaveLevelDialog
import ui.view.LevelEditState.{ChooseOperationType, ClearRectangle, Rotation}
import utilities.style.{ButtonStyles, Images}

enum LevelEditState:
  case ChooseOperationType, DefaultBasicOperations, Expand, Remove, ClearRectangle, DefaultIsometry, Rotation, Reflexion, CustomIsometry

enum RotationSelectionMode:
  case Rectangle, Pivot

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
                onApplyRotation: (Int, Int, Int, Int, ExpandMode, OverlayMode, Int, Int, RotationDirection) => Unit
                //, onBack: () => Unit
               ) {
  private var state = ChooseOperationType
  private val sidePanel: VBox = new VBox {
    spacing = 15
    padding = Insets(20)
    alignment = Pos.TopCenter
  }
  private var rectangle: Option[Rectangle] = None
  private var rotationSelectionMode: RotationSelectionMode = RotationSelectionMode.Rectangle
  private var rotationDirection: RotationDirection = RotationDirection.CW

  private var rotationPivot: Option[(Int, Int)] = None
  private var rotationExpandMode: ExpandMode = ExpandMode.NonExpanding
  private var rotationOverlayMode: OverlayMode = OverlayMode.Transparent
  private def selectedStyle: String = ButtonStyles.ButtonMouseEntered
  private def unselectedStyle: String = ButtonStyles.ButtonClassic

  private def canApplyRotation: Boolean =
    rectangle.isDefined && rotationPivot.isDefined && rotationDirection != null

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
    rectangle = None
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
      setState(LevelEditState.ChooseOperationType)
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

  private def rotationSidePanel(): Seq[javafx.scene.Node] = {
    val rectangleModeButton = new Button {
      graphic = new ImageView(Images.RectangleSelect) {
        fitWidth = 20
        fitHeight = 20
        preserveRatio = true
      }
      style =
        if rotationSelectionMode == RotationSelectionMode.Rectangle then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationSelectionMode = RotationSelectionMode.Rectangle
        updateSidePanel()
      }
    }

    val pivotModeButton = new Button {
      graphic = new ImageView(Images.PivotSelect) {
        fitWidth = 20
        fitHeight = 20
        preserveRatio = true
      }
      style =
        if rotationSelectionMode == RotationSelectionMode.Pivot then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationSelectionMode = RotationSelectionMode.Pivot
        updateSidePanel()
      }
    }

    val cwButton = new Button {
      graphic = new ImageView(Images.RotateCW) {
        fitWidth = 20
        fitHeight = 20
        preserveRatio = true
      }
      style =
        if rotationDirection == RotationDirection.CW then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationDirection = RotationDirection.CW
        updateSidePanel()
      }
    }

    val ccwButton = new Button {
      graphic = new ImageView(Images.RotateCCW) {
        fitWidth = 20
        fitHeight = 20
        preserveRatio = true
      }
      style =
        if rotationDirection == RotationDirection.CCW then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationDirection = RotationDirection.CCW
        updateSidePanel()
      }
    }
    val expandingYesButton = new Button("Да") {
      minWidth = 60
      style =
        if rotationExpandMode == ExpandMode.Expanding then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationExpandMode = ExpandMode.Expanding
        updateSidePanel()
      }
    }

    val expandingNoButton = new Button("Не") {
      minWidth = 60
      style =
        if rotationExpandMode == ExpandMode.NonExpanding then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationExpandMode = ExpandMode.NonExpanding
        updateSidePanel()
      }
    }

    val transparentYesButton = new Button("Да") {
      minWidth = 60
      style =
        if rotationOverlayMode == OverlayMode.Transparent then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationOverlayMode = OverlayMode.Transparent
        updateSidePanel()
      }
    }

    val transparentNoButton = new Button("Не") {
      minWidth = 60
      style =
        if rotationOverlayMode == OverlayMode.Opaque then selectedStyle
        else unselectedStyle

      onAction = _ => {
        rotationOverlayMode = OverlayMode.Opaque
        updateSidePanel()
      }
    }
    val applyButton = new Button("Примени") {
      minWidth = 160
      style = ButtonStyles.ButtonClassic
      disable = !canApplyRotation

      onAction = _ => {
        onApplyRotation(rectangle.get.startRow, rectangle.get.startCol, rectangle.get.endRow, rectangle.get.endCol, rotationExpandMode, rotationOverlayMode, rotationPivot.get._1, rotationPivot.get._2, rotationDirection)
        rotationExpandMode = NonExpanding
        rotationOverlayMode = Transparent
        rectangle = None
        rotationPivot = None
        refreshUI()
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

      applyButton,

      operationButton("Назад") {
        setState(LevelEditState.DefaultIsometry)
        rectangle = None
        rotationPivot = None
        refreshUI()
      }
    )
  }
  private def reflexionSidePanel(): Seq[javafx.scene.Node] = Seq(

  )
  private def customIsometrySidePanel(): Seq[javafx.scene.Node] = Seq(

  )
  private def isometrySidePanel(): Seq[javafx.scene.Node] = Seq(
    new Label("Изометрије") {
      font = Font.font(18)
    },
    operationButton("Ротација") {
      setState(LevelEditState.Rotation)
    },
    operationButton("Рефлексија") {
      setState(LevelEditState.Reflexion)
    },
    operationButton("Сложена изометрија") {
      setState(LevelEditState.CustomIsometry)
    },
    operationButton("Назад") {
      setState(LevelEditState.ChooseOperationType)
    },
    saveButton
  )
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
      case LevelEditState.Reflexion => reflexionSidePanel()
      case LevelEditState.CustomIsometry => customIsometrySidePanel()
    sidePanel.children.setAll(content *)
  }

  updateSidePanel()
  val grid: GridPane = new GridPane

    
  private def onLeftClick(row:Int, col:Int): Unit = {
    if state != ClearRectangle && state != Rotation then
      onLeft(row, col)
      refreshUI()
    else if state == ClearRectangle || rotationSelectionMode == RotationSelectionMode.Rectangle then
      rectangle match
        case None =>
          rectangle = Some(Rectangle(row, col, row, col ))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) if startRow == endRow && startCol == endCol =>
          rectangle = Some(Rectangle(rectangle.get.startRow, rectangle.get.startCol, row, col))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) =>
          rectangle = Some(Rectangle(rectangle.get.endRow, rectangle.get.endCol, row, col))

      showRectangleSelection()
    else
      rotationPivot = Some(row, col)
      showRectangleSelection()

  }

  private def showRectangleSelection(): Unit = {
    grid.children.clear()
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
    rectangle match
      case None =>
      case Some(Rectangle(startRow, startCol, endRow, endCol)) if !(startRow == endRow && startCol == endCol) =>
        val startR = Math.min(startRow, endRow)
        val endR = Math.max(startRow, endRow)
        val startC = Math.min(startCol, endCol)
        val endC = Math.max(startCol, endCol)
        for{
          row <- startR to endR
          col <- startC to endC
        } {
          buttons(row)(col).style = SelectedToClearCellView().style
          if state == ClearRectangle then
            buttons(row)(col).graphic = null
        }
      case Some(Rectangle(startRow, startCol, endRow, endCol)) if (startRow == endRow && startCol == endCol) =>
        buttons(startRow)(startCol).style = SelectedToClearCellView().style
        if state == ClearRectangle then
          buttons(startRow)(startCol).graphic = null
      case Some(_) =>
    if rotationPivot.isDefined then
      buttons(rotationPivot.get._1)(rotationPivot.get._2).style = PivotCellView().style


  }

  refreshUI()


  private def refreshUI(): Unit = {
    grid.children.clear()
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
    saveButton.disable = !isValid()
  }

  private def updateButton(btn: Button, row: Int, col: Int): Unit = {
    val cellView: LevelCellView = getCellView(row, col)
    btn.style = cellView.style
    btn.graphic = cellView.graphic.orNull
  }

  val root: HBox = new HBox {
    spacing = 30
    padding = Insets(20)
    children = Seq(grid, sidePanel)
  }
}


// TODO mozda da Rectangle ne bude tuple nego npr klasa