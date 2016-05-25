
CC  = javac -g
DOC = javadoc

.SUFFIXES: .java .class


.java.class:; $(CC) $< && chmod +x $@



CLASSFILES=UDP.class TRACE.class GMS.class GmsImpl.class PRINTOBJS.class EXAMPLE.class\
	   TUNNEL.class TunnelHeader.class FRAG.class MNAK.class HDRS.class MACK.class\
	   PT2PT.class Pt2ptFsm.class Pt2ptHeader.class FD.class UNIFORM.class PING.class\
	   PingRsp.class FLUSH.class MessageProtocolEXAMPLE.class\
	   RpcProtocolEXAMPLE.class \
	   RpcGMS.class RpcGmsImpl.class RpcClientGmsImpl.class RpcCoordGmsImpl.class\
	   RpcParticipantGmsImpl.class JoinRsp.class\
	   MERGE.class STATE_TRANSFER.class DELAY.class


all: $(CLASSFILES)



clean: 
	@rm -f *.class *~ core







