package at.ac.tuwien.ifs.tulid.group16;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class SemanticApp {

	public static void main(String[] args) {
		SpringApplication.run(SemanticApp.class);
	}

	static final String NS_BASE = "http://ifs.tuwien.ac.at/tulid/group16";

	@Bean
	public OntModel ontModel() {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		ontModel.read(Playground.class.getResourceAsStream("/university.owl"), NS_BASE, "TURTLE");
		return ontModel;
	}

	@Bean
	public Dataset dataset(@Autowired OntModel ontModel) {
		//longer alternative. 
//		Dataset ds = DatasetFactory.createTxnMem();
//		ds.begin(ReadWrite.WRITE);
//		ds.addNamedModel(SemanticApp.NS_BASE, ontModel);
//		ds.commit();
//		return ds;
		return DatasetFactory.create(ontModel);
	}
}
