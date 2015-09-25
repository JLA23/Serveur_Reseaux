package fr.lille1.iut.p2p;

import java.net.InetAddress;
import java.util.UUID;

public class PeerInfo {
	private UUID uuid;
	private InetAddress address;
	private int port;
	
	public PeerInfo(InetAddress address, int port) {
		this.uuid = UUID.randomUUID();
		this.address = address;
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String toString() {
		return uuid + ":" + address + ":" + port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerInfo other = (PeerInfo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
