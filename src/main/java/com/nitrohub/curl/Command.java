package com.nitrohub.curl;

public class Command {
	private String proxyUse;
	private String proxyHost;
	private int proxyPort;
	private String proxyUser;
	private String proxyPass;
	private String tlsProtocols;
	private String authUse;
	private String authUser;
	private String callMethod;
	private String callUrl;
	private String callEntity;
	private String callHeader;
	
	public String getCallMethod() {
		return callMethod;
	}
	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}
	public String getCallUrl() {
		return callUrl;
	}
	public void setCallUrl(String callUrl) {
		this.callUrl = callUrl;
	}
	public String getAuthUse() {
		return authUse;
	}
	public void setAuthUse(String authUse) {
		this.authUse = authUse;
	}
	public String getAuthUser() {
		return authUser;
	}
	public void setAuthUser(String authUser) {
		this.authUser = authUser;
	}
	public String getAuthPass() {
		return authPass;
	}
	public void setAuthPass(String authPass) {
		this.authPass = authPass;
	}
	private String authPass;
	
	public String getProxyUse() {
		return proxyUse;
	}
	public void setProxyUse(String proxyUse) {
		this.proxyUse = proxyUse;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public int getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	public String getProxyUser() {
		return proxyUser;
	}
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}
	public String getProxyPass() {
		return proxyPass;
	}
	public void setProxyPass(String proxyPass) {
		this.proxyPass = proxyPass;
	}
	public String getTlsProtocols() {
		return tlsProtocols;
	}
	public void setTlsProtocols(String tlsProtocols) {
		this.tlsProtocols = tlsProtocols;
	}
	public String getCallEntity() {
		return callEntity;
	}
	public void setCallEntity(String callEntity) {
		this.callEntity = callEntity;
	}
	public String getCallHeader() {
		return callHeader;
	}
	public void setCallHeader(String callHeader) {
		this.callHeader = callHeader;
	}
}
