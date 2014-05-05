package xdcc.web.resource

import fr.xdcc.api.model.MongoBot
import fr.xdcc.api.infrastructure.persistence.mongo.MongoBotService
import xdcc.web.marshaller.Format
import xdcc.web.marshaller.Marshaller
import xdcc.web.resource.bot.MongoBotResource
import org.bson.types.ObjectId
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class MongoBotResourceTest extends Specification {

  MongoBotResource mongoBotResource
  MongoBotService mongoBotService
  Marshaller<MongoBot> mongoBotMarshaller

  def setup() {
    mongoBotResource = new MongoBotResource()
    mongoBotService = Mock(MongoBotService)
    mongoBotMarshaller = Mock(Marshaller)

    mongoBotResource.mongoBotService = mongoBotService
    mongoBotResource.mongoBotMarshaller = mongoBotMarshaller
  }

  def "list"() {
    given: "some mocked bots"
    MongoBot bot1 = Mock(MongoBot)
    bot1.name >> "bot1"

    MongoBot bot2 = Mock(MongoBot)
    bot2.name >> "bot2"

    def botList = [bot1, bot2]

    and:
    mongoBotService.list() >> botList
    mongoBotMarshaller.marshall(bot1, Format.SHORT) >> marshallBot(bot1)
    mongoBotMarshaller.marshall(bot2, Format.SHORT) >> marshallBot(bot2)

    when: "list method is called"
    def result = mongoBotResource.list()

    then: "the returned list should contain the marshalled bots"
    result == [marshallBot(bot1), marshallBot(bot2)]
  }

  def "show"() {
    given: "a mocked bot"
    MongoBot bot = Mock(MongoBot)
    ObjectId id = new ObjectId()
    bot.id >> id
    bot.name >> "bot"

    and:
    mongoBotService.get(bot.id) >> bot
    mongoBotMarshaller.marshall(bot, Format.FULL) >> marshallBot(bot)

    when: "show method is called"
    def result = mongoBotResource.show(id.toStringMongod())

    then: "the returned map should contain the marshalled bot"
    result == marshallBot(bot)
  }

  private static Map marshallBot(MongoBot bot) {
    return [name: bot.name]
  }
}