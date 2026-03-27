package ui.view.components

import model.Difficulty
import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}
import scalafx.stage.{FileChooser, Stage}
import ui.dialog.ShowResultsDialog

import java.io.File

class TopMenu(
             onNewGame: () => Unit,
             onRestart: () => Unit,
             onSaveGame: () => Unit,
             onLoadSaved: () => Unit,
             onLoadMoves: () => Unit,
             onMakeNewLevel: () => Unit,
             getDifficulty: () => Difficulty,
             loadResults: Difficulty => Seq[model.Score]
             ) {

  
  val menuBar: MenuBar = new MenuBar {

    menus = List(

      new Menu("Игра") {
        items = List(
          new MenuItem("Нова игра") {
            onAction = _ => onNewGame()
          },
          new MenuItem("Почни поново") {
            onAction = _ => onRestart()
          },
          new SeparatorMenuItem,
          new MenuItem("Сачувај игру") {
            onAction = _ => onSaveGame()
          },
          new SeparatorMenuItem,
          new MenuItem("Изађи из игре") {
            onAction = _ => System.exit(0)
          }
        )
      },

      new Menu("Учитај") {
        items = List(
          new MenuItem("Направи нови ниво") {
            onAction = _ => onMakeNewLevel()
          },
          new MenuItem("Настави сачувани ниво") {
            onAction = _ => onLoadSaved()
          },
          new MenuItem("Одиграј секвенцу потеза") {
            onAction = _ => {
              onLoadMoves()
            }
          }
        )
      },

      new Menu("Резултати") {
        items = List(
          new MenuItem("Најбољи резултати") {
            onAction = _ => 
              val dialog = new ShowResultsDialog(getDifficulty(), loadResults)
              dialog.show()
          }
        )
      }

    )
  }

}
