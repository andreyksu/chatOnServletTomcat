package ru.andreyksu.annikonenkov.webapp.authorization;

import java.io.IOException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;

import ru.andreyksu.annikonenkov.webapp.userrepresent.IUser;
import ru.andreyksu.annikonenkov.webapp.userrepresent.User;

public class Authorization implements IAuthorization {

	private final Logger ___log;

	private final DataSource _dataSource;

	private final Map<String, String> _mapOfAuthUser;

	public Authorization(Logger log, DataSource dataSource, Map<String, String> mapOfAuthUser) {
		___log = log;
		_dataSource = dataSource;
		_mapOfAuthUser = mapOfAuthUser;
	}

	@Override
	public boolean authorizedInSystem(String email, String password) throws IOException {
		___log.debug("Проверяем, авторизован ли пользователь. Метод authorizedInSystem(String, String)");
		if (isAuthorizedUser(email, password)) {
			___log.debug("Пользователь есть в локальном кэше, проверка проводилась по email и password");
			return true;
		}
		___log.debug("Пользователь не прошел проверку по email и password в локальном кэше, будем смотреть в БД");
		IUser user = new User(email, ___log, _dataSource);
		String passwordOfUser = null;
		if (user.isExistUserInSystem()) {
			passwordOfUser = user.getPassword();
		}
		if (passwordOfUser != null && passwordOfUser.equals(password) && user.isUserActive()) {
			___log.debug("Пользователь по БД прошел проверку. Помещаем в локальный кэш");
			_mapOfAuthUser.put(email, password);
			return true;
		}
		___log.debug("Пользователь ни по БД ин в локальном кэше не прошел проверку");
		return false;
	}

	@Override
	public boolean isAuthorizedUser(String email) {
		___log.debug("Проверяем в локальном кэше наличие авторизованного пользователя. Проверка только по логину!");
		if (_mapOfAuthUser.containsKey(email)) {
			___log.debug("Логин {} есть в локальном кэше", email);
			return true;
		}
		return false;
	}

	// TODO: возможно нужно проверять изначально на наличие логина, а потом на
	// соответствие пароля, т.е. если здесь логин есть а пароль не
	// соответствует, что бы в БД не лезть а сразу откинуть
	@Override
	public boolean isAuthorizedUser(String email, String password) {
		___log.debug("Проверяем в локальном кэше наличие авторизованного пользователя. Проверка по логину и паролю!");
		String passOfAuthUser = _mapOfAuthUser.get(email);
		if (passOfAuthUser != null && passOfAuthUser.equals(password)) {
			___log.debug("Полученный пароль = %s И извлеченный пароль = %s Для логина = %s", password, passOfAuthUser, email);
			return true;
		}
		return false;
	}

	@Override
	public void unAuthorizedUser(String email) {
		___log.debug("Удаляем из локального кэша авторизованного пользователя. Видимо разлогинился!");
		_mapOfAuthUser.remove(email);
	}

	// TODO: возможно нужно сделать сразу после регистрации еще авторизацию,
	// т.е. добавление Map
	@Override
	public boolean registrate(String email, String password, String name) throws IOException {
		IUser user = new User(email, ___log, _dataSource);
		return user.registrateUser(password, name);
	}

}
