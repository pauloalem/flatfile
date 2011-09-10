package br.com.pr0g.flatfile.examples
/**
 * For the SimpleFlatFileReading example
 */
class SampleClass {
	
	BigDecimal aNumber
	Integer aNother
	
	static layout = {
		aNumber range: 0..10, converter: { new BigDecimal(it[0..8] + "." + it[9..10]) }
		aNother range: 11..16, converter: "Integer"
	}
}