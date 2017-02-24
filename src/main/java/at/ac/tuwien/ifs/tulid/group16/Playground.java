package at.ac.tuwien.ifs.tulid.group16;

import java.util.List;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.fuseki.embedded.FusekiEmbeddedServer;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

public class Playground {

	public static void main(String[] args) {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		ontModel.read(Playground.class.getResourceAsStream("/university.owl"), SemanticApp.NS_BASE, "TURTLE");

		// remove test individuals
		removeIndividuals(ontModel.getOntClass(SemanticApp.NS_BASE + "#Course"));
		removeIndividuals(ontModel.getOntClass(SemanticApp.NS_BASE + "#Professor"));
		// QueryExecutionFactory.createre
		// DatasetAccessorFactory.createHTTP("http://192.168.99.100");
		Dataset ds = DatasetFactory.createTxnMem();
		LogCtl.setJavaLogging();
		//this gives us an http server to query.
		//but do we actually need that? probably not so the dataset is sufficient. 
		FusekiEmbeddedServer server = FusekiEmbeddedServer
				.create()
				.add("/ds", ds)
				.build()
				.start();
		ds.begin(ReadWrite.WRITE);
		ds.addNamedModel(SemanticApp.NS_BASE, ontModel);
		ds.commit();
		//can be tested with http://localhost:3330/ds
		System.out.println("Press Enter to Quit");
		try(Scanner sc = new Scanner(System.in)) {
			sc.nextLine();
			server.stop();
			server.join();
		}
	}

	private static void removeIndividuals(OntClass c) {
		toStream(c.listInstances()).forEach(r -> {
			System.out.println(r.toString());
		});
		List<? extends OntResource> l = 
				toStream(c.listInstances()).collect(Collectors.toList());
		l.forEach(r -> r.remove());
	}

	private static Stream<? extends OntResource> toStream(ExtendedIterator<? extends OntResource> sourceIterator) {
		Stream<? extends OntResource> orstream = StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(sourceIterator, Spliterator.ORDERED), false);
		return orstream;
	}
}
