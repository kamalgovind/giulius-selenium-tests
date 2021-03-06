package com.mastfrog.testmain.suites;

import com.mastfrog.giulius.annotations.processors.IndexGeneratingProcessor;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tim Boudreau
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes({"com.mastfrog.testmain.suites.Suites"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SuitesProcessor extends IndexGeneratingProcessor {
	private static final Pattern whitespace = Pattern.compile("\\s");

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton(Suites.class.getName());
	}

	@Override
	protected boolean handleProcess(Set<? extends TypeElement> annotations, RoundEnvironment round) {
		for (Element el : round.getElementsAnnotatedWith(Suites.class)) {
			TypeElement te = (TypeElement) el;
			Suites suites = te.getAnnotation(Suites.class);
			for (String suiteName : suites.value()) {
				if (suiteName.contains(":")) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Suite name may not contain a colon: '" + suiteName + "'", te);
					continue;
				}
				if (whitespace.matcher(suiteName).matches()) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Suite name may not contain whitespace: '" + suiteName + "'", te);
					continue;
				}
				
				String line = suiteName + ":" + te.getQualifiedName();
				super.addLine(Suites.SUITES_FILE, line, el);
			}
		}
		return false;
	}
}
