package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BIRTHDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DRUG_ALLERGY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ILLNESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NRIC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.predicates.BirthdateContainsKeywordsPredicate;
import seedu.address.model.person.predicates.DrugAllergyContainsKeywordsPredicate;
import seedu.address.model.person.predicates.EmailContainsKeywordsPredicate;
import seedu.address.model.person.predicates.GenderContainsKeywordsPredicate;
import seedu.address.model.person.predicates.IllnessContainsKeywordsPredicate;
import seedu.address.model.person.predicates.NameContainsKeywordsPredicate;
import seedu.address.model.person.predicates.NricContainsKeywordsPredicate;
import seedu.address.model.person.predicates.PhoneContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    private static final Logger logger = Logger.getLogger(FindCommandParser.class.getName());
    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args,
                        PREFIX_NRIC,
                        PREFIX_NAME,
                        PREFIX_GENDER,
                        PREFIX_BIRTHDATE,
                        PREFIX_PHONE,
                        PREFIX_EMAIL,
                        PREFIX_DRUG_ALLERGY,
                        PREFIX_ILLNESS);

        if (!atLeastOnePrefixPresent(argMultimap, PREFIX_NRIC, PREFIX_NAME, PREFIX_BIRTHDATE, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_GENDER, PREFIX_DRUG_ALLERGY, PREFIX_ILLNESS)
                || !argMultimap.getPreamble().isEmpty()) {
            logger.log(Level.WARNING , "Parsing failed due to invalid prefixes");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NRIC, PREFIX_NAME, PREFIX_BIRTHDATE, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_GENDER, PREFIX_DRUG_ALLERGY, PREFIX_ILLNESS);

        String[] userKeywords;
        String[] validatedKeywords;

        // Extract search parameters based on prefixes
        ArrayList<Predicate<Person>> predicates = new ArrayList<>();

        if (argMultimap.getValue(PREFIX_NRIC).isPresent()) {
            userKeywords = formatKeywords(argMultimap.getValue(PREFIX_NRIC).get());
            validatedKeywords = new String[userKeywords.length];

            for (int i = 0; i < userKeywords.length; i++) {
                validatedKeywords[i] = ParserUtil.parseNric(userKeywords[i]).toString();
            }
            predicates.add(new NricContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            validatedKeywords = formatKeywords(
                    ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()).toString());
            predicates.add(new NameContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_GENDER).isPresent()) {
            int maxValidKeywordLength = 6;
            userKeywords = formatKeywords(argMultimap.getValue(PREFIX_GENDER).get());
            validatedKeywords = new String[userKeywords.length];

            for (int i = 0; i < userKeywords.length; i++) {
                String gender = ParserUtil.parseGender(userKeywords[i]).toString();
                if (gender.length() > 1) {
                    gender = gender.substring(0, maxValidKeywordLength);
                }
                validatedKeywords[i] = gender;
            }
            predicates.add(new GenderContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_BIRTHDATE).isPresent()) {
            userKeywords = formatKeywords(argMultimap.getValue(PREFIX_BIRTHDATE).get());
            validatedKeywords = new String[userKeywords.length];

            for (int i = 0; i < userKeywords.length; i++) {
                validatedKeywords[i] = ParserUtil.parseBirthDate(userKeywords[i]).toString();
            }
            predicates.add(new BirthdateContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            userKeywords = formatKeywords(argMultimap.getValue(PREFIX_PHONE).get());
            validatedKeywords = new String[userKeywords.length];

            for (int i = 0; i < userKeywords.length; i++) {
                validatedKeywords[i] = ParserUtil.parsePhone(userKeywords[i]).toString();
            }
            predicates.add(new PhoneContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            userKeywords = formatKeywords(argMultimap.getValue(PREFIX_EMAIL).get());
            validatedKeywords = new String[userKeywords.length];

            for (int i = 0; i < userKeywords.length; i++) {
                validatedKeywords[i] = ParserUtil.parseEmail(userKeywords[i]).toString();
            }
            predicates.add(new EmailContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_DRUG_ALLERGY).isPresent()) {
            validatedKeywords = formatKeywords(
                    ParserUtil.parseDrugAllergy(
                            argMultimap.getValue(PREFIX_DRUG_ALLERGY).get()).toString());
            predicates.add(new DrugAllergyContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        if (argMultimap.getValue(PREFIX_ILLNESS).isPresent()) {
            validatedKeywords = formatKeywords(
                    ParserUtil.parseFindIllness(argMultimap.getValue(PREFIX_ILLNESS).get()));
            predicates.add(new IllnessContainsKeywordsPredicate(Arrays.asList(validatedKeywords)));
        }

        assert !predicates.isEmpty() : "Predicates list should not be empty before combining";
        Predicate<Person> combinedPredicate = predicates.stream().reduce(Predicate::and).orElse(first -> true);
        logger.log(Level.INFO, "Combined predicates for FindCommand");
        return new FindCommand(combinedPredicate);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean atLeastOnePrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    private String[] formatKeywords(String medicalRecord) {
        return medicalRecord.split("\\s+");
    }
}

