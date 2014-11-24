package com.ehensin.tunnel.client.session;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.DefaultCallBack;
import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.locator.LocatorHelper;
import com.ehensin.tunnel.client.protocol.MsgProtocol;
import com.ehensin.tunnel.client.protocol.MsgRepProtocol;
import com.ehensin.tunnel.client.protocol.ProtocolHelper;
import com.ehensin.tunnel.client.tunnel.Tunnel;
import com.ehensin.tunnel.client.tunnel.TunnelRequest;
import com.ehensin.tunnel.client.tunnel.TunnelResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatelsessSession extends BaseSession {
	private final Logger logger = LoggerFactory
			.getLogger(StatefulSession.class);
	private long creationTime;
    private long sessionId;
    private long lastAccessTime;
    private long timeout;
    private Map<String, Object> attributes;
    /* 当前session是否有效 */
	private boolean isValidate = true;
	/*tunnel this session associated*/
    private Tunnel tunnel;
	
    public StatelsessSession(long sessionId){
    	this(sessionId,LocatorHelper.getRRLocatorFactor(), LocatorHelper.getRRLocatorFactor());
	}
    
    StatelsessSession(long sessionId,LocatorFactor<?> pf, LocatorFactor<?> tf){
		super();
		this.sessionId = sessionId;
		this.creationTime = System.currentTimeMillis();
		this.lastAccessTime = this.creationTime;
		this.timeout = -1;
		this.tunnel = this.getTunnel(pf, tf);
		attributes = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public long getCreationTime() {
		return this.creationTime;
	}

	@Override
	public long getId() {
		return this.sessionId;
	}

	@Override
	public long getLastAccessTime() {
		return this.lastAccessTime;
	}

	@Override
	public long getTimeout() {
		return this.timeout;
	}

	synchronized public boolean isValidate(){
		return isValidate;
	}

	
	@Override
	synchronized public void invalidate() {
		this.attributes.clear();
		isValidate = false;
	}

	@Override
	public void setAttribute(String name, Object obj) {
		attributes.put(name, obj);
	}

	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}


	@SuppressWarnings("unused")
	@Override
	public Object syncInvoke(String serviceName, String function, Object[] params,
			Class<?> resultClass)
			throws SessionException {
		if( logger.isDebugEnabled() ){
			logger.debug("sync invoke remote call, serviceName:{} function:{} params:{}",new Object[]{serviceName, function, params});
		}
		this.lastAccessTime = System.currentTimeMillis();
		MsgProtocol msg = ProtocolHelper.getRPCReqMessage(serviceName, function, params, tunnel.getRemoteIp(), resultClass) ;
		
		TunnelRequest req = new TunnelRequest(msg, LocatorFactor.NULLFactorObj);
		req.setIsSyncCall(true);
		
		TunnelResponse rep = tunnel.invoke(req);
		if( rep.isSuccess() ){
			/*sync call*/
			if( rep.getResponseObj() != null ){
				ObjectMapper mapper = new ObjectMapper();
				Object result = null;
				MsgRepProtocol b = null;
				try {
					MsgProtocol invokeResult = (MsgProtocol)rep.getResponseObj();
					b = (MsgRepProtocol)invokeResult.getBody();
					if( b.getStatusCode() == ProtocolHelper.REP_STATUS_OK)
					    result = mapper.readValue(b.getResult(), resultClass);
					else{						
						throw new SessionException("remote return exception : " + b.getException(), b.getErrorCode());
					}
				} catch (IOException ex) {
					throw new SessionException("cannot decode result:" + rep.getResponseObj(), ex, ErrorCodeEnum.CodecDecodeError.getCode());
				}
			   return result;
			}
			throw new SessionException("response result is null, src msg id : " + rep.getSrcMsgId());
		}else{
			if( rep.getResponseObj() != null && rep.getResponseObj() instanceof Throwable)
			    throw new SessionException(rep.getErrorDescription(),(Throwable)rep.getResponseObj(), rep.getErrorCode().getCode());
			else
				throw new SessionException(rep.getErrorDescription(), rep.getErrorCode().getCode());
		}
	}
	
	
	public String asyncInvoke(String serviceName, String function, Object[] params,
			DefaultCallBack<?> callback)
			throws SessionException {
		if( logger.isDebugEnabled() ){
			logger.debug("async invoke remote call, serviceName:{} function:{} params:{}",new Object[]{serviceName, function, params});
		}
		this.lastAccessTime = System.currentTimeMillis();
		MsgProtocol msg = ProtocolHelper.getRPCReqMessage(serviceName, function, params, tunnel.getRemoteIp(), callback.getCallbackResultClass()) ;	
		TunnelRequest req = new TunnelRequest(msg, LocatorFactor.NULLFactorObj);
		req.setIsSyncCall(false);
		/*async call*/
		if( callback != null )
		    this.cacheCall(msg.getMsgHeader().getMsgId(), callback);
		TunnelResponse rep = this.tunnel.invoke(req);
		if( rep.isSuccess() ){
			return msg.getMsgHeader().getMsgId();
			
		}else{
			this.removeCacheCall(msg.getMsgHeader().getMsgId());
			if( rep.getResponseObj() != null && rep.getResponseObj() instanceof Throwable)
			    throw new SessionException(rep.getErrorDescription(),(Throwable)rep.getResponseObj(), rep.getErrorCode().getCode());
			else
				throw new SessionException(rep.getErrorDescription(), rep.getErrorCode().getCode());
		}
	}
	
	
	public String syncPlainInvoke(String plainText) throws SessionException{
		if( logger.isDebugEnabled() ){
			logger.debug("plain sync invoke， text:{}",plainText);
		}
		this.lastAccessTime = System.currentTimeMillis();
		MsgProtocol msg = ProtocolHelper.getPlainReqMessage(plainText, tunnel.getRemoteIp()) ;
		
		TunnelRequest req = new TunnelRequest(msg, LocatorFactor.NULLFactorObj);
		req.setIsSyncCall(true);
		
		TunnelResponse rep = tunnel.invoke(req);
		if( rep.isSuccess() ){
			/*sync call*/
			if( rep.getResponseObj() != null ){
				String result = null;
				MsgRepProtocol b = null;
				
				MsgProtocol invokeResult = (MsgProtocol)rep.getResponseObj();
				b = (MsgRepProtocol)invokeResult.getBody();
				if( b.getStatusCode() == ProtocolHelper.REP_STATUS_OK)
				    result = b.getResult();
				else{						
					throw new SessionException("remote return exception : " + b.getException(), b.getErrorCode());
				}
				
			   return result;
			}
			throw new SessionException("response result is null, src msg id : " + rep.getSrcMsgId());
		}else{
			if( rep.getResponseObj() != null && rep.getResponseObj() instanceof Throwable)
			    throw new SessionException(rep.getErrorDescription(),(Throwable)rep.getResponseObj(), rep.getErrorCode().getCode());
			else
				throw new SessionException(rep.getErrorDescription(), rep.getErrorCode().getCode());
		}
	}
	public String asyncPlainInvoke(String plainText, DefaultCallBack<?> callback) throws SessionException{
		if( logger.isDebugEnabled() ){
			logger.debug("plain async invoke， text:{}",plainText);
		}
		this.lastAccessTime = System.currentTimeMillis();
		MsgProtocol msg = ProtocolHelper.getPlainReqMessage(plainText, tunnel.getRemoteIp()) ;	
		TunnelRequest req = new TunnelRequest(msg, LocatorFactor.NULLFactorObj);
		req.setIsSyncCall(false);
		/*async call*/
		if( callback != null )
		    this.cacheCall(msg.getMsgHeader().getMsgId(), callback);
		TunnelResponse rep = this.tunnel.invoke(req);
		if( rep.isSuccess() ){
			return msg.getMsgHeader().getMsgId();
			
		}else{
			this.removeCacheCall(msg.getMsgHeader().getMsgId());
			if( rep.getResponseObj() != null && rep.getResponseObj() instanceof Throwable)
			    throw new SessionException(rep.getErrorDescription(),(Throwable)rep.getResponseObj(), rep.getErrorCode().getCode());
			else
				throw new SessionException(rep.getErrorDescription(), rep.getErrorCode().getCode());
		}
	}
}
