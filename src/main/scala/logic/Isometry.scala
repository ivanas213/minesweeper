package logic

import model.Level

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
  case Diagonal1(row: Int, col: Int)
  case Diagonal2(row: Int, col: Int)
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

case class IsometryBaseConfiguration(
                                      rectangle: Rectangle,
                                      expand: ExpandMode,
                                      overlay: OverlayMode
                                    )

sealed trait Isometry extends (Level => Level) {

  protected def validateRectangle(level: Level, rectangle: Rectangle): Either[String, Unit] = {
    val rect = rectangle.normalize()

    if level.cells.isEmpty then
      Left("Ниво је празан.")
    else {
      val rows = level.cells.length
      val cols = level.cells.head.length

      if rect.startRow < 0 || rect.startCol < 0 || rect.endRow >= rows || rect.endCol >= cols then
        Left("Изабрани правоугаоник је изван граница нивоа.")
      else
        Right(())
    }
  }

  def validate(level: Level): Either[String, Unit]

  protected def unsafeApply(level: Level): Level

  override def apply(level: Level): Level =
    validate(level) match
      case Right(()) => unsafeApply(level)
      case Left(_)   => level

  def quasiInverse: Isometry

  def chain(other: Isometry): Isometry =
    ChainedIsometry(this, other)
}

case class Rotation90(
                       config: IsometryBaseConfiguration,
                       pivotRow: Int,
                       pivotColumn: Int,
                       dir: RotationDirection
                     ) extends Isometry {

  override def validate(level: Level): Either[String, Unit] =
    for
      _ <- validateRectangle(level, config.rectangle)
      _ <- validatePivot(level)
    yield ()

  private def validatePivot(level: Level): Either[String, Unit] = {
    val rows = level.cells.length
    val cols = if rows == 0 then 0 else level.cells.head.length

    if pivotRow < 0 || pivotRow >= rows || pivotColumn < 0 || pivotColumn >= cols then
      Left("Пивот је изван граница нивоа.")
    else
      Right(())
  }

  private def mapCell(row: Int, col: Int): (Int, Int) = {
    val dr = row - pivotRow
    val dc = col - pivotColumn

    dir match
      case RotationDirection.CW  => (pivotRow + dc, pivotColumn - dr)
      case RotationDirection.CCW => (pivotRow - dc, pivotColumn + dr)
  }

  override protected def unsafeApply(level: Level): Level =
    IsometryApplier.apply(level, config, mapCell)

  override def quasiInverse: Isometry =
    copy(
      dir =
        if dir == RotationDirection.CW then RotationDirection.CCW
        else RotationDirection.CW
    )
}

case class Reflection(
                       config: IsometryBaseConfiguration,
                       axis: ReflectionAxis
                     ) extends Isometry {

  override def validate(level: Level): Either[String, Unit] =
    for
      _ <- validateRectangle(level, config.rectangle)
      _ <- validateAxis(level)
    yield ()

  private def validateAxis(level: Level): Either[String, Unit] = {
    val rows = level.cells.length
    val cols = if rows == 0 then 0 else level.cells.head.length

    axis match
      case ReflectionAxis.Vertical(col) =>
        if col < 0 || col >= cols then
          Left("Колона је изван граница.")
        else
          Right(())

      case ReflectionAxis.Horizontal(row) =>
        if row < 0 || row >= rows then
          Left("Врста је изван граница.")
        else
          Right(())

      case ReflectionAxis.Diagonal1(row, col) =>
        if row < 0 || row >= rows || col < 0 || col >= cols then
          Left("Тачка из које се одреује дијагонала је изван граница.")
        else
          Right(())

      case ReflectionAxis.Diagonal2(row, col) =>
        if row < 0 || row >= rows || col < 0 || col >= cols then
          Left("Тачка из које се одреује дијагонала је изван граница.")
        else
          Right(())
  }

  private def mapCell(row: Int, col: Int): (Int, Int) = axis match
    case ReflectionAxis.Vertical(k) =>
      (row, 2 * k - col)

    case ReflectionAxis.Horizontal(k) =>
      (2 * k - row, col)

    case ReflectionAxis.Diagonal1(pr, pc) =>
      val dr = row - pr
      val dc = col - pc
      (pr + dc, pc + dr)

    case ReflectionAxis.Diagonal2(pr, pc) =>
      val dr = row - pr
      val dc = col - pc
      (pr - dc, pc - dr)

  override protected def unsafeApply(level: Level): Level =
    IsometryApplier.apply(level, config, mapCell)

  override def quasiInverse: Isometry =
    this
}

case class ChainedIsometry(first: Isometry, second: Isometry) extends Isometry {

  override def validate(level: Level): Either[String, Unit] =
    first.validate(level) match
      case Left(error) => Left(error)
      case Right(()) =>
        val intermediate = first(level)
        second.validate(intermediate)

  override protected def unsafeApply(level: Level): Level = {
    val intermediate = first(level)
    second(intermediate)
  }

  override def apply(level: Level): Level =
    validate(level) match
      case Right(()) =>
        val intermediate = first(level)
        second(intermediate)
      case Left(_) =>
        level

  override def quasiInverse: Isometry =
    ChainedIsometry(second.quasiInverse, first.quasiInverse)
}