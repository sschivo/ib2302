//package week2;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.jupiter.api.Test;
//
//class ConfigurationTest {
//
//	@Test
//	void parseTest() {
//		String s = "g1";
//		Map<String, Configuration> configurations = new LinkedHashMap<>();
//
//		Configuration c1 = Configuration.parse(s, configurations);
//		Configuration c2 = Configuration.parse(s, configurations);
//
//		assertTrue(c1 == c2);
//		assertEquals(c1, c2);
//		assertEquals(c1.hashCode(), c2.hashCode());
//		assertEquals(c1.toString(), c2.toString());
//	}
//
//	@Test
//	void parseListTest() {
//		String s = "g1 g2 g3";
//		Map<String, Configuration> configurations = new LinkedHashMap<>();
//
//		List<Configuration> expected = Arrays.asList(Configuration.parse("g1", configurations),
//				Configuration.parse("g2", configurations), Configuration.parse("g3", configurations));
//
//		assertEquals(expected, Configuration.parseList(s, configurations));
//	}
//}
