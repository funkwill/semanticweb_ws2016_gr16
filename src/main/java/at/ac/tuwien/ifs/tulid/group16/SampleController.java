package at.ac.tuwien.ifs.tulid.group16;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.Resources;

@RestController
public class SampleController {

	public static class SampleResource {
		private String courseId;
		private String courseName;

		public SampleResource() {
			//empty
		}
		
		public SampleResource(String courseId, String courseName) {
			this.courseId = courseId;
			this.courseName = courseName;
		}

		public String getCourseId() {
			return courseId;
		}

		public String getCourseName() {
			return courseName;
		}
	}

	private Dataset dataset;

	@Autowired
	public SampleController(Dataset dataset) {
		this.dataset = dataset;
	}

	@RequestMapping(path = "/api/stuff", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<SampleResource> stuff() throws IOException {
		ArrayList<SampleResource> content = new ArrayList<>();
		dataset.begin(ReadWrite.READ);
		// QueryExecutionFactory.create(Query.lt)
		Query q = QueryFactory
				.create(Resources.toString(getClass().getResource("/queries/find-courses.rq"), Charset.defaultCharset()));
		
		try (QueryExecution qexec = QueryExecutionFactory.create(q, dataset)) {
			ResultSet rs = qexec.execSelect();

			for (; rs.hasNext();) {
				QuerySolution soln = rs.nextSolution();
				content.add(new SampleResource(
						soln.getLiteral("courseID").getString(), 
						soln.getLiteral("courseName").getString()));
			}
		} catch (RuntimeException e) {
			dataset.abort();
			throw e;
		}
		dataset.commit();
		return content;
	}
}
