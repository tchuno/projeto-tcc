package net.gnfe.util.ddd;

import net.gnfe.util.DummyUtils;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.TimerTask;

public abstract class SpringJob extends TimerTask {

	private String[] sessionFactoryName = {"sessionFactory"};
	protected ApplicationContext applicationContext;
	private Exception exception;

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setSessionFactoryName(String... sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}

	public void executeWithoutSession(){

		try {
			execute();
		}
		catch (Exception e) {

			DummyUtils.syserr(getClass().getName() + ".executeWithoutSession() > falha ao executar o job: " + e.getMessage());
			e.printStackTrace();
			exception = e;
		}
	}

	public void executeWithSessionManagement(){

		try {
			openSessions();
		}
		catch (RuntimeException e) {

			DummyUtils.syserr(getClass().getName() + ".executeWithSessionManagement() > falha ao executar o job, não foi possível criar a sessão: " + e.getMessage());
			e.printStackTrace();
			exception = e;
		}

		try {
			execute();
		}
		catch (Exception e) {

			DummyUtils.syserr(getClass().getName() + ".executeWithSessionManagement() > falha ao executar o job: " + e.getMessage());
			e.printStackTrace();
			exception = e;
		}
		finally {

			closeSessions();
		}
	}

	private void openSessions() {

		for (String sfn : sessionFactoryName) {

			SessionFactory sessionFactory = (SessionFactory) applicationContext.getBean(sfn, SessionFactory.class);
			Session session = sessionFactory.openSession();
			session.setFlushMode(FlushMode.MANUAL);

			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		}
	}

	private void closeSessions() {

		for (String sfn : sessionFactoryName) {

			SessionFactory sessionFactory = (SessionFactory) applicationContext.getBean(sfn, SessionFactory.class);
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);

			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}

	public abstract void execute() throws Exception;

	@Override
	public void run() {
		executeWithSessionManagement();
	}

	public Exception getException() {
		return exception;
	}
}
