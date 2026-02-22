package ui.view

import model.Level
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.text.Font
import ui.{ButtonCell, SaveLevelDialog}
import ui.view.LevelEditState.{ClearRectangle, Default}
import utilities.ButtonStyles

enum LevelEditState:
  case Default, Expand, Remove, ClearRectangle
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
                isValid: () => Boolean
                //, onBack: () => Unit
               ) {
  private var state = Default
  private val sidePanel: VBox = new VBox {
    spacing = 15
    padding = Insets(20)
    alignment = Pos.TopCenter
  }
  private var rectangle: Option[Rectangle] = None
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
  private def defaultSidePanel(): Seq[javafx.scene.Node] = Seq(
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
      setState(LevelEditState.Default)
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
      setState(LevelEditState.Default)
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
    operationButton("Nazad") {
      setState(LevelEditState.Default)
      rectangle = None
      refreshUI()
    }
  )

  private def updateSidePanel(): Unit = {
    val content = state match
      case LevelEditState.Default => defaultSidePanel()
      case LevelEditState.Expand => expandSidePanel()
      case LevelEditState.Remove => removeSidePanel()
      case LevelEditState.ClearRectangle => clearRectangleSidePanel()

    sidePanel.children.setAll(content *)
  }

  updateSidePanel()
  val grid: GridPane = new GridPane

    
  private def onLeftClick(row:Int, col:Int): Unit = {
    if state != ClearRectangle then
      onLeft(row, col)
      refreshUI()
    else
      rectangle match
        case None =>
          rectangle = Some(Rectangle(row, col, row, col ))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) if startRow == endRow && startCol == endCol =>
          rectangle = Some(Rectangle(rectangle.get.startRow, rectangle.get.startCol, row, col))
        case Some(Rectangle(startRow, startCol, endRow, endCol)) =>
          rectangle = Some(Rectangle(rectangle.get.endRow, rectangle.get.endCol, row, col))

      showRectangleSelection(rectangle)


  }

  private def showRectangleSelection(rectangle: Option[Rectangle]): Unit = {
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
          buttons(row)(col).graphic = null
        }
      case Some(Rectangle(startRow, startCol, endRow, endCol)) =>
        buttons(startRow)(startCol).style = SelectedToClearCellView().style
        buttons(startRow)(startCol).graphic = null

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