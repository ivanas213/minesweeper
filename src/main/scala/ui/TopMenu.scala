package ui

import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}

class TopMenu(
             onNewGame: () => Unit,
             onRestart: () => Unit,
             onSaveGame: () => Unit,
             onLoadSaved: () => Unit,
             onLoadLevel: () => Unit,
             onLoadMoves: () => Unit,
             onShowResults: () => Unit
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
          new MenuItem("Учитај сачувану игру") {
            onAction = _ => onLoadSaved()
          },
          new MenuItem("Учитај ниво из фајла") {
            onAction = _ => onLoadLevel()
          },
          new MenuItem("Одиграј секвенцу потеза") {
            onAction = _ => onLoadMoves()
          }
        )
      },

      new Menu("Резултати") {
        items = List(
          new MenuItem("Најбољи резултати") {
            onAction = _ => onShowResults()
          }
        )
      }

    )
  }

}
