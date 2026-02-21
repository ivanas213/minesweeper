import logic.{Difficulty, Expert, GameController, ScoreSaverLoader, GameSaverLoader, Level}
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
        onLoadMoves = gameController.loadMoves,
        onShowResults = () => (),
        getScore = () => gameController.getScore,
        getTime = () => gameController.getTime,
        getClicks = () => gameController.getClicks,
        getDifficulty = () => gameController.getDifficulty,
        onSaveResult = (difficulty, name, score) => ScoreSaverLoader().saveResult(difficulty, name, score),
        loadResults = difficulty => ScoreSaverLoader().loadResults(difficulty)
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
        onLoadMoves = gameController.loadMoves,
        onShowResults = () => (),
        getScore = () => gameController.getScore,
        getTime = () => gameController.getTime,
        getClicks = () => gameController.getClicks,
        getDifficulty = () => gameController.getDifficulty,
        onSaveResult = (difficulty, name, score) => ScoreSaverLoader().saveResult(difficulty, name, score),
        loadResults = difficulty => ScoreSaverLoader().loadResults(difficulty)
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




// TODO videti ono za () => sto svuda imam a vrv ne treba
// TODO za ucitavanje nivoa mozda sortiranje po datumu/ispis datuma/brisanje
// TODO sloziti GameController da ne bude haotican
// TODO razvrstati klase u potpakete (npr views)
// TODO sta znaci Unit => Unit
// TODO videti za ono isEnd i isLost sto je realno gledano glupo
// TODO sat i zastavica da budu lepsi
// TODO mozda da ne moze da se sacuva igra ako status nije Playing ako to vec nemam
// TODO pregledati ceo kod
// TODO videti da li negde gde je funkcija moze samo rezultat funkcije
// TODO zavrsiti sve stavke iz menija i obrisati visak ako ga ima
// TODO videti za ovo dirketno pisanje umesto u konstruktor
// TODO sve sto se ne koristi sa strane da bude private znaci proci kroz ceo kod
// TODO popuniti sve prazne metode iz gameview konstruktora
// TODO isto i za dipliciran kod
// TODO da mi se levi klik u ui svuda na isti nacin radi
// TODO videti to sto su neki nivoi preveliki
// TODO da li moraju da se potrose zastavice da se uverim da je sig okej da mogu da odu u minus
// TODO dodati jos koji Intermediate i Expert nivo
// TODO dodati preostale exceptione i napraviti klasu gde se cuvaju poruke
// TODO napraviti ikonicu igre uz naziv mozda
// TODO srediti lepo sve one load i save
// TODO da se ne cuva na levi klik ako je gotova igra
// TODO sekvenca poteza
// TODO srediti ovo za ispis rezultata da bude malo lepse i preglednije i videti kako se inace to radi u igricama
// TODO videti za pauziranje vremena npr dok se gledaju rezultati i kad god igra nije u fokusu
// TODO proci kroz svaki TODO u svim klasama redom
// TODO videti gde je bolje da imam pravu ikonicu  (vrv svuda)
// TODO videti koji Exception treba kada da se baci
// TODO videti za mozda neko prilagodjavanje velicine polja (npr ako je preveliki ili premali nivo)
// TODO kada se restaruje igra nakon load game da li je okej da se restartuje do mesta gde je ucitana
// TODO videti za moje silne wrappere koji menjaju imena na cudan nacin
// TODO mozda neki shortcuts npr enter za potvrdu ctrl + s za save ili tako nesto
// TODO x dugme na dijalogu ako nede fali a mislim da ne
// TODO nazad kod izbora tezine
// TODO mozda i u meniju load game
// TODO Mozda da se cuva i naziv samog nivoa kad se cuva nivo
// TODO da ne moze da se sacuva vise puta nego se npr pregazi stara sacuvana ako je ista igra
// TODO za hint obavezno da ne moze vise odjednom
// TODO videti za require
// TODO mozda da row i col pripada nekoj klasi npr Coordinate mada previse je posla i vrv nije vredno toga
// TODO videti za redove koji imaju previse koda
// TODO smisleti bolji naziv od ovog Bomb