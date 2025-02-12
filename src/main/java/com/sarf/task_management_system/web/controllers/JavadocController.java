package com.sarf.task_management_system.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для предоставления доступа к документации Javadoc.
 * <p>
 * Этот контроллер обрабатывает запросы на получение Javadoc и перенаправляет их на соответствующий HTML-файл документации.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/docs")
public class JavadocController {

	/**
	 * Обрабатывает запрос на получение Javadoc.
	 * <p>
	 * При получении запроса этот метод перенаправляет пользователя на страницу с Javadoc.
	 * </p>
	 *
	 * @return строка с указанием URL для перенаправления на Javadoc.
	 */
	@GetMapping("/javadoc")
	public String get() {
		log.trace("Taken javadoc request");
		return "redirect:/documents/javadoc.html";
	}
}
