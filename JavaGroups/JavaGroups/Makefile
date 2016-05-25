
CC  = javac -g
DOC = javadoc

.SUFFIXES: .java .class


.java.class:; $(CC) $< && chmod +x $@


PACKAGES=JavaGroups JavaGroups.Ensemble JavaGroups.Algorithms\
	 JavaGroups.JavaStack JavaGroups.JavaStack.Protocols JavaGroups.Gui

BASE_DIR=JavaGroups

JAR=jar



CLASSFILES=Channel.class Message.class Event.class MembershipListener.class\
	   MessageListener.class Transport.class\
	   SyncCall.class Queue.class MessageCorrelator.class QueueClosed.class\
	   ChannelNotConnected.class PullPushAdapter.class Dispatcher.class\
	   MethodCall.class RemoteMethodCall.class MethodInvoker.class ChannelEntry.class\
	   LazyEvaluator.class Header.class JChannel.class Conf.class\
	   ChannelFactory.class JChannelFactory.class\
	   Util.class Profiler.class MethodLookup.class MethodLookupClos.class\
	   MethodLookupJava.class Timeout.class DistributedHashtable.class\
	   RepeatedUnicast.class Timer.class Command.class SlidingWindow.class\
	   Version.class AckSenderWindow.class AckReceiverWindow.class\
           ChannelException.class BlockEvent.class SuspectEvent.class ChannelClosed.class\
	   EnsChannel.class EnsChannelFactory.class RequestCorrelator.class\
           RspCollector.class RequestHandler.class View.class AndCommand.class OrCommand.class\
	   Rsp.class RspList.class Suspected.class GetStateEvent.class SetStateEvent.class\
	   Subject.class Observer.class







This: $(CLASSFILES)
all: $(CLASSFILES) subdirs documentation
ibus: IbusChannel.class IbusChannelFactory.class
JavaGroups.jar: $(CLASSFILES) TestFiles subdirs ibus
	@(cd .. ;\
	$(JAR) -cvf JavaGroups.jar JavaGroups/*.class JavaGroups/Ensemble/*.class \
	            JavaGroups/JavaStack/*.class JavaGroups/JavaStack/Protocols/*.class\
	            JavaGroups/Algorithms/*.class JavaGroups/Tests/wb/*.class)
src: 
	@(cd .. ;\
	tar -cvf src.tar JavaGroups/*.java JavaGroups/Ensemble/*.java \
	            JavaGroups/JavaStack/*.java JavaGroups/JavaStack/Protocols/*.java\
	            JavaGroups/Algorithms/*.java)


demos:; @(cd Tests; make all)

subdirs:
	@(cd Algorithms          ; make)
	@(cd JavaStack           ; make)
	@(cd JavaStack/Protocols ; make)
	@(cd Ensemble            ; make)




documentation:
	$(DOC) -d doc -author -version -public $(PACKAGES)


clean: 
	@rm -f ./doc/*.html ./doc/html~ ./doc/*~ core *.class *.class~
	@(cd Algorithms          ; make clean)
	@(cd JavaStack           ; make clean)
	@(cd JavaStack/Protocols ; make clean)
	@(cd Ensemble            ; make clean)
	@(cd tests               ; make clean)
	@(cd Tests               ; make clean)
	@(cd Tests/ColorDemo     ; make clean)
	@(cd Tests/Grid          ; make clean)
	@(cd Tests/GMS           ; make clean)
	@(cd Tests/Applet        ; make clean)
	@(cd Tests/wb            ; make clean)


TestFiles:
	@(cd Tests/wb            ; make)

wc:;
	wc -l *.java Algorithms/*.java JavaStack/*.java JavaStack/Protocols/*.java\
              Ensemble/*.java


