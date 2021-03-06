package gov.uspto.patent.model.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class CpCClassificationTest {

	private static Map<String, String> validFromTo = new LinkedHashMap<String, String>();
	static {
		validFromTo.put("A01B300", "A01B 3/00");
		validFromTo.put("A01B3/00", "A01B 3/00");
		validFromTo.put("A01B33/00", "A01B 33/00");
		validFromTo.put("D07B22012051", "D07B 2201/2051");
		validFromTo.put("D07B 2201/2051", "D07B 2201/2051");
		validFromTo.put("D07B2201/2051", "D07B 2201/2051");
		validFromTo.put("D07B2201/20903", "D07B 2201/20903");
		validFromTo.put("D21", "D21");
		validFromTo.put("D21B", "D21B");
		validFromTo.put("D21B 1/00", "D21B 1/00");
	}

	@Test(expected = ParseException.class)
	public void failBlank() throws ParseException {
		CpcClassification cpc = new CpcClassification();
		cpc.parseText("");
	}

	@Test
	public void validParseCheck() throws ParseException {
		for (Entry<String,String> check: validFromTo.entrySet()){
			CpcClassification cpc = new CpcClassification();
			cpc.parseText(check.getKey());

			assertEquals( check.getValue(), cpc.getTextNormalized());
		}
	}

	@Test
	public void testEquals() throws ParseException {
		CpcClassification cpc1 = new CpcClassification();
		cpc1.parseText("D07B2201");
		
		CpcClassification cpc2 = new CpcClassification();
		cpc2.parseText("D07B2201");

		assertEquals(cpc1, cpc2);
	}

	@Test
	public void testEqualsUnder() throws ParseException {
		CpcClassification cpc1 = new CpcClassification();
		cpc1.parseText("D07B");
		
		CpcClassification cpc2 = new CpcClassification();
		cpc2.parseText("D07B2201");
		
		assertTrue(cpc1.isContained(cpc2));
	}

	@Test
	public void testToTextNormalized() throws ParseException {
		CpcClassification cpcClass = new CpcClassification();
		cpcClass.parseText("D07B2201/2051");
		
		String expect = "D07B 2201/2051";
		assertEquals(expect, cpcClass.getTextNormalized());
	}

	@Test
	public void testStandardize() throws ParseException {
		CpcClassification cpcClass = new CpcClassification();
		cpcClass.parseText("D07B2201/2051");
		String expect = "D07B022012051";
		assertEquals(expect, cpcClass.standardize());
	}

	@Test
	public void filterCPC() throws ParseException {
		Set<PatentClassification> clazs = new TreeSet<PatentClassification>();
		
		CpcClassification cpcClass = new CpcClassification();
		cpcClass.parseText("D21");
		cpcClass.setInventive(true);
		clazs.add(cpcClass);
		
		CpcClassification cpcClass2 = new CpcClassification();
		cpcClass2.parseText("D07B2201/2051");
		cpcClass2.setInventive(false);
		clazs.add(cpcClass2);
		
		CpcClassification cpcClass3 = new CpcClassification();
		cpcClass3.parseText("D07B2201");
		cpcClass3.setInventive(false);
		clazs.add(cpcClass3);

		Map<String, List<CpcClassification>> cpcClasses = CpcClassification.filterCpc(clazs);
		assertEquals(cpcClasses.get("inventive").size(), 1);
		assertEquals(cpcClass, cpcClasses.get("inventive").get(0));
		
		assertEquals(cpcClasses.get("additional").size(), 2);
		assertEquals(cpcClass3, cpcClasses.get("additional").get(0));
		assertEquals(cpcClass2, cpcClasses.get("additional").get(1));
	}
}
