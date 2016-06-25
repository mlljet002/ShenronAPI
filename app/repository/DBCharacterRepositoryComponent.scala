package repository

import java.sql.{Connection, DriverManager, ResultSet}

import main.scala.cake.CharacterRepositoryComponent
import model.Character
import play.api.Play

/**
  * Implementation for the CharacterRepository component which provides a memory character
  * repository.
  *
  * @author jorge
  * @since 19/06/16
  */
trait DBCharacterRepositoryComponent extends CharacterRepositoryComponent {

  override val characterRepo = new DBCharacterRepository

  class DBCharacterRepository extends CharacterRepository {

    val driver = Play.configuration.getString("db.default.driver")
    val url = Play.configuration.getString("db.default.url")
    val user = Play.configuration.getString("db.default.user")
    val password = Play.configuration.getString("db.default.password")

    def connectToDatabase: Connection = {
      Class.forName(driver.get)
      DriverManager.getConnection(url.get, user.get, password.get)
    }

    override def create(character: Character) {
      val connection = connectToDatabase
      connection.createStatement.executeQuery(createCharacterQuery(character))
      connection.close()
    }

    override def update(character: Character) {
      val connection = connectToDatabase
      connection.createStatement.executeQuery(updateCharacterQuery(character))
      connection.close()
    }

    override def delete(id: Long) {
      val connection = connectToDatabase
      connection.createStatement.executeQuery(deleteCharacterQuery(id))
      connection.close()
    }

    override def findAll(): List[Character] = {
      val connection = connectToDatabase
      val resultSet = connection.createStatement.executeQuery(findAllQuery())
      connection.close()

      List()
    }

    override def find(id: Long): Option[Character] = {
      val connection = connectToDatabase
      val resultSet = connection.createStatement.executeQuery(findByIdQuery(id))
      connection.close()
      None
    }

    override def find(name: String): Option[Character] = {
      val connection = connectToDatabase
      val resultSet = connection.createStatement.executeQuery(findByName(name))
      connection.close()
      None
    }

    private def createCharacterQuery(character: Character): String = {
      "INSERT INTO characters (name, description, photourl) VALUES " +
        character.name + ", " + character.description + ", " + character.photoUrl
    }

    private def updateCharacterQuery(character: Character): String = {
      "UPDATE characters SET " +
        "name=" + character.name + ", " +
        "description=" + character.description + ", " +
        "photourl=" + character.photoUrl + " WHERE id=" + character.id
    }

    private def deleteCharacterQuery(id: Long): String = {
      "DELETE FROM characters WHERE id=" + id
    }

    def findAllQuery(): String = {
      "SELECT * FROM characters"
    }

    def findByIdQuery(id: Long) = {
      "SELECT FROM characters WHERE id=" + id
    }

    def findByName(name: String) = {
      "SELECT FROM characters WHERE name=" + name
    }

    def resultSetStream(resultSet: ResultSet): Stream[String] = {
      new Iterator[String] {
        def hasNext = resultSet.next()

        def next() = resultSet.getString(1)
      }.toStream
    }
  }

}
