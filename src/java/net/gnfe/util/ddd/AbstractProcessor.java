package net.gnfe.util.ddd;

import net.gnfe.util.DummyUtils;

public abstract class AbstractProcessor extends SpringJob implements Runnable {

	private Thread thread;
	private boolean finalizado;
	private Exception exception;

	@Override
	public void run() {

		try {
			executeWithSessionManagement();
		}
		finally {
			DummyUtils.sysout(DummyUtils.getClassName(getClass().getSimpleName()) + ".run() processamento finalizado.");
			setFinalizado(true);
		}
	}

	@Override
	public final void execute() {

		try {
			execute2();
		}
		catch (Exception e) {
			e.printStackTrace();
			exception = e;
		}
	}

	protected abstract void execute2() throws Exception;

	public void start() {

		DummyUtils.sysout(DummyUtils.getClassName(getClass().getSimpleName()) + ".start() iniciando thread.");

		thread = new Thread(this);
		thread.start();
	}

	public boolean isFinalizado() {
		return finalizado || thread == null || !thread.isAlive();
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public Exception getError() {

		if(exception != null) {
			return exception;
		}

		return super.getException();
	}

/*	@Override
	@Deprecated
	public RuntimeException getException() {
		return super.getException();
	}*/
}
