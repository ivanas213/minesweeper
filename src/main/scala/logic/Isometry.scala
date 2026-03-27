package logic

import model.Level
import utilities.enumeration.ErrorMessage

enum ExpandMode {
  case Expanding, NonExpanding
}

enum OverlayMode {
  case Transparent, Opaque
}

enum RotationDirection {
  case CW, CCW
}

enum ReflectionAxis {
  case Vertical(col: Int)
  case Horizontal(row: Int)
  case DiagonalMain(row: Int, col: Int)
  case DiagonalSecondary(row: Int, col: Int)
}

case class Rectangle(startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
  def normalize(): Rectangle =
    Rectangle(
      Math.min(startRow, endRow),
      Math.min(startCol, endCol),
      Math.max(startRow, endRow),
      Math.max(startCol, endCol)
    )
}

case class IsometryConfiguration(
                                             rectangle: Rectangle,
                                             expand: ExpandMode,
                                             overlay: OverlayMode
                                           )



sealed trait Isometry {

  def mapPoint(row: Int, col: Int): (Int, Int)

  protected def validateRectangle(level: Level, rectangle: Rectangle): Either[String, Unit] = {
    val rect = rectangle.normalize()

    if level.cells.isEmpty then
      Left(ErrorMessage.emptyLevel)
    else
      val rows = level.cells.length
      val cols = level.cells.head.length

      if rect.startRow < 0 || rect.startCol < 0 || rect.endRow >= rows || rect.endCol >= cols then
        Left(s"${ErrorMessage.selectedRectangleOutOfBounds}  ${rect.startRow}  ${rect.startCol}  ${rect.endRow}  ${rect.endCol}  ${rows} ${cols}")
      else
        Right(())
  }

  def validate(
                level: Level,
                config: IsometryConfiguration
              ): Either[String, Unit]

  protected def applyIsometry(
                             level: Level,
                             config: IsometryConfiguration
                           ): Level

  final def run(
                 level: Level,
                 config: IsometryConfiguration
               ): Either[String, Level] =
    validate(level, config).map(_ => applyIsometry(level, config))

  def quasiInverse: Isometry

  def chain(otherIsometry: Isometry): CompositeIsometry =
    this match
      case composite: CompositeIsometry =>
        CompositeIsometry(composite.steps :+ otherIsometry)
      case _ =>
        CompositeIsometry(List(this, otherIsometry))
}

case class Rotation90(
                       dir: RotationDirection,
                       pivot: (Int, Int)
                     ) extends Isometry {

  override def mapPoint(row: Int, col: Int): (Int, Int) = {
    val pivotRow = pivot._1
    val pivotCol = pivot._2
    val rowDiff = row - pivotRow
    val colDiff = col - pivotCol

    dir match
      case RotationDirection.CW =>
        (pivotRow + colDiff, pivotCol - rowDiff)
      case RotationDirection.CCW =>
        (pivotRow - colDiff, pivotCol + rowDiff)
  }

  override def validate(
                         level: Level,
                         config: IsometryConfiguration
                       ): Either[String, Unit] =
    for
      _ <- validateRectangle(level, config.rectangle)
      _ <- validatePivot(level)
    yield ()

  private def validatePivot(level: Level): Either[String, Unit] = {
    val rows = level.cells.length
    val cols = if rows == 0 then 0 else level.cells.head.length

    if pivot._1 < 0 || pivot._1 >= rows || pivot._2 < 0 || pivot._2 >= cols then
      Left(ErrorMessage.pivotOutOfBounds)
    else
      Right(())
  }

  override protected def applyIsometry(
                                      level: Level,
                                      config: IsometryConfiguration
                                    ): Level =
    IsometryApplier.apply(
      level,
      config,
      (row, col) => mapPoint(row, col)
    )

  override def quasiInverse: Isometry =
    Rotation90(
      if dir == RotationDirection.CW then RotationDirection.CCW
      else RotationDirection.CW,
      pivot
    )
}

case class Reflection(axis: ReflectionAxis) extends Isometry {

  override def validate(
                         level: Level,
                         config: IsometryConfiguration
                       ): Either[String, Unit] =
    for
      _ <- validateRectangle(level, config.rectangle)
      _ <- validateAxis(level)
    yield ()

  private def validateAxis(level: Level): Either[String, Unit] = {
    val rows = level.cells.length
    val cols = if rows == 0 then 0 else level.cells.head.length

    axis match
      case ReflectionAxis.Vertical(col) =>
        if col < 0 || col >= cols then Left(ErrorMessage.colOutOfBounds)
        else Right(())

      case ReflectionAxis.Horizontal(row) =>
        if row < 0 || row >= rows then Left(ErrorMessage.rowOutOfBounds)
        else Right(())

      case ReflectionAxis.DiagonalMain(row, col) =>
        if row < 0 || row >= rows || col < 0 || col >= cols then
          Left(ErrorMessage.diagonalOutOfBounds)
        else
          Right(())

      case ReflectionAxis.DiagonalSecondary(row, col) =>
        if row < 0 || row >= rows || col < 0 || col >= cols then
          Left(ErrorMessage.diagonalOutOfBounds)
        else
          Right(())
  }

  override def mapPoint(row: Int, col: Int): (Int, Int) =
    axis match
      case ReflectionAxis.Vertical(axis) =>
        (row, 2 * axis - col)

      case ReflectionAxis.Horizontal(axis) =>
        (2 * axis - row, col)

      case ReflectionAxis.DiagonalMain(axisR, axisC) =>
        val dr = row - axisR
        val dc = col - axisC
        (axisR + dc, axisC + dr)

      case ReflectionAxis.DiagonalSecondary(axisR, axisC) =>
        val dr = row - axisR
        val dc = col - axisC
        (axisR - dc, axisC - dr)

  override protected def applyIsometry(
                                      level: Level,
                                      config: IsometryConfiguration
                                    ): Level =
    IsometryApplier.apply(
      level,
      config,
      (row, col) => mapPoint(row, col)
    )

  override def quasiInverse: Isometry = this
}

case class CompositeIsometry(steps: List[Isometry]) extends Isometry {

  override def mapPoint(row: Int, col: Int): (Int, Int) =
    steps.foldLeft((row, col)) { case ((currentRow, currentCol), isometry) =>
      isometry.mapPoint(currentRow, currentCol)
    }

  override def validate(
                         level: Level,
                         config: IsometryConfiguration
                       ): Either[String, Unit] =
    validateRectangle(level, config.rectangle)

  override protected def applyIsometry(
                                        level: Level,
                                        config: IsometryConfiguration
                                      ): Level =
    IsometryApplier.apply(
      level,
      config,
      (row, col) => mapPoint(row, col)
    )
  
  

  private def getNewRectangle(
                               level: Level,
                               rectangle: Rectangle,
                               isometry: Isometry,
                               expandMode: ExpandMode
                             ): Either[String, Rectangle] = {
    val rect = rectangle.normalize()

    val point1 = isometry.mapPoint(rect.startRow, rect.startCol)
    val point2 = isometry.mapPoint(rect.startRow, rect.endCol)
    val point3 = isometry.mapPoint(rect.endRow, rect.startCol)
    val point4 = isometry.mapPoint(rect.endRow, rect.endCol)

    val rowsList = List(point1._1, point2._1, point3._1, point4._1)
    val colsList = List(point1._2, point2._2, point3._2, point4._2)

    val newRectangle = Rectangle(
      rowsList.min,
      colsList.min,
      rowsList.max,
      colsList.max
    )

    val rows = level.cells.length
    val cols = if rows == 0 then 0 else level.cells.head.length

    val rowMove =
      if newRectangle.startRow < 0 then -newRectangle.startRow
      else 0

    val colMove =
      if newRectangle.startCol < 0 then -newRectangle.startCol
      else 0

    val movedRectangle = Rectangle(
      newRectangle.startRow + rowMove,
      newRectangle.startCol + colMove,
      newRectangle.endRow + rowMove,
      newRectangle.endCol + colMove
    )

    val outOfBounds =
      movedRectangle.endRow >= rows || movedRectangle.endCol >= cols

    if (rowMove > 0 || colMove > 0 || outOfBounds) && expandMode == ExpandMode.NonExpanding then
      Left("Правоугаоник излази ван граница нивоа.")
    else
      Right(movedRectangle)
  }

  override def quasiInverse: Isometry =
    CompositeIsometry(
      steps.reverse.map(_.quasiInverse)
    )

  override def chain(otherIsometry: Isometry): CompositeIsometry =
    CompositeIsometry(steps :+ otherIsometry)
}

object CompositeIsometry {
  def apply(first: Isometry, second: Isometry, rest: Isometry*): CompositeIsometry =
    CompositeIsometry((first +: second +: rest).toList)

  
}

object CentralSymmetry {
  def apply(pivot: (Int, Int)): Isometry =
    Rotation90(RotationDirection.CW, pivot)
      .chain(Rotation90(RotationDirection.CW, pivot))
}

object TranslationHorizontal {
  def apply(shift: Int): Isometry =
    Reflection(ReflectionAxis.Vertical(0))
      .chain(Reflection(ReflectionAxis.Vertical(shift)))
}

object TranslationVertical {
  def apply(shift: Int): Isometry =
    Reflection(ReflectionAxis.Horizontal(0))
      .chain(Reflection(ReflectionAxis.Horizontal(shift)))
}
object Translation {
  def apply(shiftHorizontal: Int, shiftVertical:Int): Isometry =
    TranslationHorizontal(shiftHorizontal).chain(TranslationVertical(shiftVertical))
}