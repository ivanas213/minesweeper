{Board, Cell, Empty, Flagged, Hidden, Mine, Revealed}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, HBox, VBox}
import ui.ButtonCell
import ui.LevelUtilities.loadLevel
import scalafx.scene.image.{Image, ImageView}


val mineImg = new Image(getClass.getResourceAsStream("/mine.png"))


object Main extends JFXApp3 {

  val mineImg = new Image(getClass.getResourceAsStream("/bomb.png"))
  val flagImg = new Image(getClass.getResourceAsStream("/flag.png"))
  def refreshUI(): Unit = {
    if(!currentBoard.isPlayable) disableButtons()
    for {
      r <- 0 until currentBoard.rows
      c <- 0 until currentBoard.cols
    } {
      val cell = currentBoard.cellAt(r, c).get
      val btn = buttons(r)(c)
      if(currentBoard.isPlayable){
        cell.state match {

          case Hidden =>
            btn.text = ""
            btn.style =
              s"""
                        -fx-background-color: ${Colors.Empty};
                        -fx-border-color: #555555;
                        -fx-border-width: 0.5;
                      """
            btn.graphic = null
          case Revealed =>
            //btn.disable = true

            cell.data match {
              case Mine =>
                btn.text = ""
                btn.graphic = new ImageView(mineImg) {
                  fitWidth = 14
                  fitHeight = 14
                  preserveRatio = true
                }
                btn.style = "-fx-background-color: red;"

              case Empty =>
                btn.text =
                  if (currentBoard.neighborMines(r, c) > 0) currentBoard.neighborMines(r, c).toString
                  else ""
                btn.style =
                  s"""
                         -fx-background-color: ${Colors.RevealedEmpty};
                         -fx-text-fill: ${Colors.NumberColors.getOrElse(currentBoard.neighborMines(r, c), Color.Black)};
                         -fx-font-weight: bold;
                       """
                btn.graphic = null
            }

          case Flagged =>
            btn.text = ""
            btn.style =
              s"""
                                  -fx-background-color: ${Colors.Empty};
                                  -fx-border-color: #555555;
                                  -fx-border-width: 0.5;
                                """
            btn.graphic = new ImageView(flagImg) {
              fitWidth = 12
              fitHeight = 12
              preserveRatio = true
            }
        }
      }
      else {
        cell.state match {

          case Hidden if cell.data == Mine =>
            btn.text = ""
            btn.graphic = new ImageView(mineImg) {
              fitWidth = 14
              fitHeight = 14
              preserveRatio = true
            }
            btn.style = s"-fx-background-color: ${Colors.Empty};"
          case Hidden if cell.data != Mine =>
            btn.text = ""
            btn.graphic = null
            btn.style = s"-fx-background-color: ${Colors.Empty};"
          case Revealed =>
            cell.data match {
              case Mine =>
                btn.text = ""
                btn.graphic = new ImageView(mineImg) {
                  fitWidth = 14
                  fitHeight = 14
                  preserveRatio = true
                }
                btn.style = "-fx-background-color: red;"

              case Empty =>
                btn.text =
                  if (currentBoard.neighborMines(r, c) > 0) currentBoard.neighborMines(r, c).toString
                  else ""
                btn.style =
                  s"""
                         -fx-background-color: ${Colors.RevealedEmpty};
                         -fx-text-fill: ${Colors.NumberColors.getOrElse(currentBoard.neighborMines(r, c), Color.Black)};
                         -fx-font-weight: bold;
                       """
                btn.graphic = null
            }

          case Flagged =>
            btn.text = ""
            btn.style =
              s"""
                                  -fx-background-color: ${Colors.Empty};
                                  -fx-border-color: #555555;
                                  -fx-border-width: 0.5;
                                """
            btn.graphic = new ImageView(flagImg) {
              fitWidth = 12
              fitHeight = 12
              preserveRatio = true
            }
        }
      }
      
    }
  }

  private var currentBoard = loadLevel("C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\intermediate\\level2.txt")
  private val buttons: Array[Array[ButtonCell]] = Array.ofDim[ButtonCell](currentBoard.rows, currentBoard.cols)

  def onCellLeftClick(row: Int, col: Int): Unit = {
    currentBoard = currentBoard.revealCell(row, col)
    refreshUI()
  }
  
  def onCellRightClick(row:Int, col: Int): Unit ={
    currentBoard = currentBoard.flagCell(row, col)
    refreshUI()
  }
  def disableButtons(): Unit = {
    buttons.foreach { row =>
      row.foreach { button => 
        {
          button.onAction = null
          button.onMouseClicked = null
        }
        
      }
    }

  }
  private def createBoard(rows: Int, cols: Int): GridPane = {
    val grid = new GridPane{

    }
    for (r <- 0 until rows; c <- 0 until cols) {
      val button = new ButtonCell(r, c, onCellLeftClick, onCellRightClick)
      buttons(r)(c) = button
      grid.add(button, c, r)
    }
    grid
  }

  override def start(): Unit = {

    val grid = createBoard(currentBoard.rows, currentBoard.cols)
    stage = new PrimaryStage {
      title = "Minesweeper"
      scene = new Scene {
        root = grid
      }
    }
  }


}