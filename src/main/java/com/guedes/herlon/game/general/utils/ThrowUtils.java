package com.guedes.herlon.game.general.utils;

import com.guedes.herlon.game.exceptions.InvalidThrowException;
import com.guedes.herlon.game.exceptions.NoFileException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.model.interfaces.ThrowDetails;
import com.guedes.herlon.game.model.ThrowDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ThrowUtils {

	private final FileUtils fileUtils;

	public ThrowUtils(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public ThrowDetails recoverThrowDetailsFrom(String line, String splitter) {
		String[] throwDetails = line.split(splitter);

		if(throwDetails.length != 2) {
			return new ThrowDetailsImpl();
		}
		return new ThrowDetailsImpl(throwDetails[0], throwDetails[1].toUpperCase());
	}

	public List<ThrowDetails> getThrowDetailsFrom(String file) throws IOException {
		try {
			Set<ConstraintViolation<ThrowDetails>> constraintViolations = new HashSet<>();

			List<ThrowDetails> throwDetailsList = new ArrayList<>();
			List<String> lines = fileUtils.getLinesFromFile(file);
			lines.forEach(line -> {
				ThrowDetails throwDetails = recoverThrowDetailsFrom(line, Constants.FILE_LINE_ELEMENT_SPLITTER);
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
}
