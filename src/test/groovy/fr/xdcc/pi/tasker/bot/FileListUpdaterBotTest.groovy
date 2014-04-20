package fr.xdcc.pi.tasker.bot

import fr.xdcc.pi.tasker.service.TaskerService
import org.jibble.pircbot.DccFileTransfer
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class FileListUpdaterBotTest extends Specification {

  FileListUpdaterBot bot
  TaskerService taskerService

  def setup() {
    taskerService = Mock(TaskerService)
    bot = new FileListUpdaterBot("name", 1)
    bot.taskerService = taskerService
  }

  def "onFileTransferFinished - The bot still has remaining tasks"() {
    given: "the bot has two tasks"
    bot = new FileListUpdaterBot('name', 2)
    bot.taskerService = taskerService

    and: "a File"
    File file = new File("spock.txt")

    and: "a mocked DccFileTransfer"
    DccFileTransfer dccFileTransfer = Mock(DccFileTransfer)
    dccFileTransfer.getFile() >> file
    dccFileTransfer.getNick() >> "sender"

    when: "onFileTransferFinished is fired"
    bot.onFileTransferFinished(dccFileTransfer, null)

    then: "updateAvailableFiles method of TaskerService should be called one time"
    1 * dccFileTransfer.close()
    1 * taskerService.updateAvailableFiles(dccFileTransfer.getFile(), dccFileTransfer.getNick())
  }

  def "onFileTransferFinished - The bot has finished all his tasks"() {
    given: "a File"
    File file = new File("spock.txt")

    and: "a mocked DccFileTransfer"
    DccFileTransfer dccFileTransfer = Mock(DccFileTransfer)
    dccFileTransfer.getFile() >> file
    dccFileTransfer.getNick() >> "sender"

    when: "onFileTransferFinished is fired"
    bot.onFileTransferFinished(dccFileTransfer, null)

    then: "updateAvailableFiles method of TaskerService should be called one time"
    1 * taskerService.updateAvailableFiles(dccFileTransfer.getFile(), dccFileTransfer.getNick())
  }

  def "onFileTransferFinished - Transfer went wrong"() {
    given: "a File"
    File file = new File("spock.txt")

    and: "a mocked DccFileTransfer"
    DccFileTransfer dccFileTransfer = Mock(DccFileTransfer)
    dccFileTransfer.getFile() >> file
    dccFileTransfer.getNick() >> "sender"

    when: "onFileTransferFinished is fired"
    bot.onFileTransferFinished(dccFileTransfer, new IllegalArgumentException("message"))

    then: "updateAvailableFiles method of TaskerService should be called one time"
    0 * taskerService.updateAvailableFiles(dccFileTransfer.getFile(), dccFileTransfer.getNick())
  }

  def "file is sent by an unauthorized entity"() {
    given: "a File"
    File file = new File("spock.txt")

    and: "a mocked DccFileTransfer"
    DccFileTransfer dccFileTransfer = Mock(DccFileTransfer)
    dccFileTransfer.getFile() >> file
    dccFileTransfer.getNick() >> "Unauthorized"

    when: "onIncomingFileTransfer is fired"
    bot.onIncomingFileTransfer(dccFileTransfer)

    then: "the tranfer should not be proceeded"
    0 * dccFileTransfer.receive(_ as File, _ as boolean)
    1 * dccFileTransfer.close()
  }

  def "file is received when sender is authorized"() {
    given: "a File"
    File file = new File("spock.exe")
    File savedFile = new File("lists/spock.exe.txt")

    and: "a mocked DccFileTransfer"
    DccFileTransfer dccFileTransfer = Mock(DccFileTransfer)
    dccFileTransfer.getFile() >> file
    dccFileTransfer.getNick() >> "[SeriaL]Xdcc`authorized"

    when: "onIncomingFileTransfer is fired"
    bot.onIncomingFileTransfer(dccFileTransfer)

    then: "the tranfer should not be proceeded"
    1 * dccFileTransfer.receive(savedFile, false)
    0 * dccFileTransfer.close()
  }

  def "assertion fails when user list is received from unwanted channel"() {
    when: "onUserList is fired"
    bot.onUserList("#wrong_channel", null)

    then: "an AssertionError is thrown"
    thrown(AssertionError)
  }
}
