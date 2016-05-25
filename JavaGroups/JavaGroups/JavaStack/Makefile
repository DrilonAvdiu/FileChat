\
CC  = javac -g
DOC = javadoc

.SUFFIXES: .java .class


.java.class:; $(CC) $< && chmod +x $@



CLASSFILES=ProtocolStack.class Protocol.class Configurator.class \
	   ViewId.class\
	   Membership.class Gossip.class \
	   GossipServer.class GossipClient.class \
	   JRouter.class RegistrationRequest.class Request.class Address.class\
	   MessageProtocol.class RpcProtocol.class


all: $(CLASSFILES)



clean: 
	@rm -f *.class *~ core


#GossipServer.class: GossipServer.java Gossip.java
#	javac $< && chmod +x $<
#	rmic JavaGroups.JavaStack.GossipServer







