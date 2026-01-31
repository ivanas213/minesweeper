package logic

sealed trait Difficulty {
  def name: String
  def levels: Vector[Level]
}

case object Beginner extends Difficulty{
  val name = "Почетни ниво"
  val levels: Vector[Level] = Vector(
    Level("Ниво 1", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level1.txt"),
    Level("Ниво 2", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level2.txt"),
    Level("Ниво 3", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level3.txt"),
    Level("Ниво 4", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level4.txt"),
    Level("Ниво 5", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level5.txt"),
    Level("Ниво 6", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level6.txt"),
    Level("Ниво 7", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level7.txt"),
    Level("Ниво 8", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level8.txt"),
    Level("Ниво 9", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level9.txt"),
    Level("Ниво 10", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level10.txt"),
    Level("Ниво 11", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level11.txt"),
    Level("Ниво 12", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level12.txt"),
    Level("Ниво 13", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level13.txt"),
    Level("Ниво 14", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\beginner\\level14.txt")

  )
}
case object Intermediate extends Difficulty{
  val name = "Средњи ниво"
  val levels: Vector[Level] = Vector(
    Level("Ниво 1", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\intermediate\\level1.txt"),
    Level("Ниво 2", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\intermediate\\level2.txt")
  )
}

case object Expert extends Difficulty{
  val name = "Напредни ниво"
  val levels: Vector[Level] = Vector(
    Level("Ниво 1", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\expert\\level1.txt"),
    Level("Ниво 2", "C:\\Users\\Ivana\\Desktop\\Desktop\\FP\\Mine\\src\\main\\scala\\levels\\expert\\level2.txt")
  )
}
