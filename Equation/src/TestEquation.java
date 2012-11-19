import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestEquation {
	
	@DataProvider(name="equation")
	public Object[][] createData(){
		return new Object[][]{
			{1,1l},{32327, 656502l},{40921, 686720l}
		};
	}
	
	@Test
	public void testGeneratePrimes(){
		long time = System.currentTimeMillis();
		Solution sol = new Solution(100);
		List<Integer> primesBelow100 = Arrays.asList(new Integer[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97});
		List<Integer> primesGenerated = sol.generatePrimesBelowN();
		AssertJUnit.assertTrue(CollectionUtils.isEqualCollection(primesGenerated, primesBelow100));
		System.out.println(System.currentTimeMillis() - time);
	}
	
	@Test(dataProvider="equation")
	public void testSolution(Integer input, Long output){
		Solution sol = new Solution(input);
		AssertJUnit.assertEquals(output.intValue(), sol.numberOfSolutionsMod1000007());
	}

}
