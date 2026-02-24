package utilities.style

class CellStyles {
  val HiddenCell = s"""
     -fx-background-color: linear-gradient(to bottom, ${Colors.AlmostWhite}, ${Colors.LittleGray});
     -fx-background-radius: 4;
     -fx-border-color: ${Colors.Border};
     -fx-border-radius: 4;
     -fx-border-width: 0.8;
     -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0.3, 0, 1);
   """
  val MineToRevealStyle =  s"""
     -fx-background-color: linear-gradient(to bottom, ${Colors.AlmostWhite}, ${Colors.LittleGray});
     -fx-background-radius: 4;
     -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0.3, 0, 1);
   """
  val Mine = s"""
     -fx-background-color: linear-gradient(to bottom, ${Colors.Red1}, ${Colors.Red2});
     -fx-background-radius: 4;
     -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0.3, 0, 1);
   """
  val Flagged: String = HiddenCell
  val SelectedToClear = s"""
     -fx-background-color: linear-gradient(to bottom, ${Colors.VeryLightBlue}, ${Colors.LightBlue});
     -fx-background-radius: 4;
     -fx-border-color: ${Colors.Border};
     -fx-border-radius: 4;
     -fx-border-width: 0.8;
     -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0.3, 0, 1);
   """
  def EmptyRevealed(number: Int) = s"""
           -fx-background-color: ${Colors.RevealedEmpty};
           -fx-border-color: ${Colors.Border};
           -fx-border-radius: 4;
           -fx-border-width: 0.8;
           -fx-text-fill: ${Colors.NumberColors.getOrElse(number, Colors.RevealedEmpty)};
           -fx-font-weight: bold;
          """
  def Empty: String = HiddenCell
  def Hint = s"""
     -fx-background-color: linear-gradient(to bottom, ${Colors.Green1}, ${Colors.Green2});
     -fx-background-radius: 4;
     -fx-border-color: ${Colors.Border};
     -fx-border-radius: 4;
     -fx-border-width: 0.8;
     -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0.3, 0, 1);
   """
}
