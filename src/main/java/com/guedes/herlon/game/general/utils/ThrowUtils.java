package com.guedes.herlon.game.general.utils;

import com.guedes.herlon.game.exceptions.InvalidThrowException;
import com.guedes.herlon.game.exceptions.NoFileException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.model.interfaces.ThrowDetails;
import com.guedes.herlon.game.model.ThrowDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utils class to work with Throw records.
 * @author herlon-guedes
 * @since 08/19/2020
 */
@Slf4j
@Service
public class ThrowUtils {

	private final FileUtils fileUtils;

	@Autowired
	public ThrowUtils(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	/**
	 * Uses the file param to create a list of ThrowDetails instances.
	 * @param file absolute path to the file containing the details of the throws
	 * @return List of ThrowDetails instances
	 * @throws IOException when occurs a problem while opening the file
	 */
	public List<ThrowDetails> getThrowDetailsFrom(String file) throws IOException {
		try {
			Set<ConstraintViolation<ThrowDetails>> constraintViolations = new HashSet<>();

			List<ThrowDetails> throwDetailsList = new ArrayList<>();
			List<String> lines = fileUtils.getLinesFromFile(file);
			lines.forEach(line -> {
				ThrowDetails throwDetails = recoverThrowDetailsFrom(line);
				throwDetailsList.add(throwDetails);
				constraintViolations.addAll(throwDetails.validate());
			});

			if(!constraintViolations.isEmpty()) {
				constraintViolations.forEach(throwDetailsConstraintViolation -> log.error(throwDetailsConstraintViolation.getMessage()));
				throw new InvalidThrowException("Error on getThrowDetailsFrom execution. The throw format is invalid");
			}

			return throwDetailsList;
		} catch (Exception e) {
			String errorMessage = "Error on getThrowDetailsFrom execution. Error while reading file line from file " + file;
			log.error(errorMessage, e);
			throw new NoFileException(errorMessage);
		}
	}

	/**
	 * Splits a line using the Constants.FILE_LINE_ELEMENT_SPLITTER and creates a ThrowDetails instance using the split result.
	 * @see Constants#FILE_LINE_ELEMENT_SPLITTER
	 * @param line string containing the details of a throw
	 * @return instance of ThrowDetails
	 */
	private ThrowDetails recoverThrowDetailsFrom(String line) {
		String[] throwDetails = line.split(Constants.FILE_LINE_ELEMENT_SPLITTER);

		if(throwDetails.length != 2) {
			return new ThrowDetailsImpl();
		}
		return new ThrowDetailsImpl(throwDetails[0], throwDetails[1].toUpperCase());
	}
}
