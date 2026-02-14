import logic.{Difficulty, GameController, GameSaverLoader, Level}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import ui.{GameView, SceneController}


object Main extends JFXApp3  {
  override def start(): Unit = {
    val stage = new PrimaryStage

    def startGame(level: Level): Unit = {
      val gameController = new GameController(levelPath = Some(level.path))

      val gameView = new GameView(
        rows = gameController.rows,
        cols = gameController.cols,
        onLeft = gameController.onLeftClick,
        onRight = gameController.onRightClick,
        getCellView = gameController.getCellView,
        flagsLeft = () => gameController.getState.flags,
        getHintCoordinates = () => gameController.getHintCoordinates,
        isEnded = () => gameController.isEnded,
        isLost = () => gameController.isLost,
        onNewGame = () => (),
        onRestart = () => gameController.restart(),
        onSaveGame = gameController.saveGame,
        onLoadSaved = () => (),
        onLoadLevel = () => (),
        onLoadMoves = () => (),
        onShowResults = () => ()
      )
      gameController.setOnTimeChanged(seconds =>
        gameView.updateTime(seconds)
      )
      stage.scene = new Scene {
        root = gameView.root
      }
    }

    def loadGame(name:String): Unit = {
      val gameController = new GameController(initialGameState = Some(GameSaverLoader.loadGame(name)) )

      val gameView = new GameView(
        rows = gameController.rows,
        cols = gameController.cols,
        onLeft = gameController.onLeftClick,
        onRight = gameController.onRightClick,
        getCellView = gameController.getCellView,
        flagsLeft = () => gameController.getState.flags,
        getHintCoordinates = () => gameController.getHintCoordinates,
        isEnded = () => gameController.isEnded,
        isLost = () => gameController.isLost,
        onNewGame = () => (),
        onRestart = () => gameController.restart(),
        onSaveGame = gameController.saveGame,
        onLoadSaved = () => (),
        onLoadLevel = () => (),
        onLoadMoves = () => (),
        onShowResults = () => ()
      )
      gameController.setOnTimeChanged(seconds =>
        gameView.updateTime(seconds)
      )
      stage.scene = new Scene {
        root = gameView.root
      }
    }
    def getLevels(difficulty: Difficulty) = difficulty.levels
    val sceneController = new SceneController(stage, startGame, () => GameSaverLoader.getSavedGamesNames, getLevels, loadGame)
    sceneController.showStartGame()

  }
}

// videti to sto su neki nivoi preveliki
// da li moraju da se potrose zastavice

// restart dugme
// hint logika

// da li da ogranicim broj zastavica

// pracenje rezultata i logika za racunanje rezultata
// validacija nivoa

// da smajlic restartuje

// videti za ovo sto velicina prelazi
// videti za zatvaranje stage-a, back i tako to
// videti za moje silne wrappere koji menjaju imena na cudan nacin
// mozda neki shortcuts npr enter za potvrdu ctrl + s za save ili tako nesto
// videti ono za () => sto svuda imam a vrv ne treba
// x dugme na dijalogu
// mozda videti sta ako ima razmak u nazivu kad se cuva igra ili rezultat
// za ucitavanje nivoa mozda sortiranje po datumu/ispis datuma/brisanje
// sloziti GameController da ne bude haotican
// razvrstati klase u potpakete (npr views)
// sta znaci Unit => Unit
// videti za ono isEnd i isLost sto je realno gledano glupo
// sat i zastavica da budu lepsi
// mozda da ne moze da se sacuva igra ako status nije Playing ako to vec nemam
// pregledati ceo kod
// videti da li negde gde je funkcija moze samo rezultat funkcije
// zavrsiti sve stavke iz menija i obrisati visak ako ga ima
// videti za ovo dirketno pisanje umesto u konstruktor
// sve sto se ne koristi sa strane da bude private znaci proci kroz ceo kod
// popuniti sve prazne metode iz gameview konstruktora
// isto i za dipliciran kod
// da mi se levi klik u ui svuda na isti nacin radi