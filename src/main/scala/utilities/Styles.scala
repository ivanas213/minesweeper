package utilities

object Styles {
  val HiddenCell = s"""
                              -fx-background-color: ${Colors.Empty};
                              -fx-border-color: #555555;
                              -fx-border-width: 0.5;
                            """
  val MineToRevealStyle =  "-fx-background-color: ${Colors.Empty};"
  val Mine = "-fx-background-color: red;"
  val Flagged = s"""
                                        -fx-background-color: ${Colors.Empty};
                                        -fx-border-color: #555555;
                                        -fx-border-width: 0.5;
                                      """
  val Empty = s"""
           -fx-background-color: ${Colors.RevealedEmpty};
           -fx-text-fill: ${Colors.NumberColors.getOrElse(neighbors, Colors.RevealedEmpty)};
           -fx-font-weight: bold;
          """
}
