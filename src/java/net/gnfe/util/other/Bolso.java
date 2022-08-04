package net.gnfe.util.other;

public class Bolso<T> {

	private T objeto;
	private long startTime;
	private long finalTime;

	public Bolso() { }

	public Bolso(T objeto) {
		this.objeto = objeto;
	}

	public Bolso(T objeto, long finalTime) {
		this.objeto = objeto;
		this.finalTime = finalTime;
	}

	public boolean expirou() {

		long currentTimeMillis = System.currentTimeMillis();
		return currentTimeMillis > finalTime;
	}

	public void setObjeto(T objeto) {
		this.objeto = objeto;
	}

	public T getObjeto() {
		return objeto;
	}

	public long getFinalTime() {
		return finalTime;
	}

	public void setFinalTime(long finalTime) {
		this.finalTime = finalTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}