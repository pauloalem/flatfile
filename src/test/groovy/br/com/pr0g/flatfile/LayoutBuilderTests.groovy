package br.com.pr0g.flatfile;

class ExampleLayout {
	BigDecimal aNumber
	Integer aNother

	static final layout = {
		aNumber range: 0..10, converter: { new BigDecimal(it[0..7] + "." + it[8..9]) }
		aNother range: 10..15, converter: "Integer"
	}
}

class LayoutBuilderTests extends GroovyTestCase {

	static final String sampleData = "0000004225000100"

	public void setUp() {
	}

	public void tearDown() {
	}

	def checkSample(sample) {
		assertEquals 42.25, sample.aNumber
		assertEquals 100, sample.aNother
	}

	void testClassBuild() {
		def sample = new LayoutBuilder()
		.with(sampleData)
		.build(ExampleLayout)

		checkSample(sample)
	}

	void testExpandoBuild() {
		def sample = new LayoutBuilder(ExampleLayout.layout)
		.with(sampleData)
		.build()
		checkSample(sample)
	}

	void testObjectBind() {
		def sample = new ExampleLayout()
		def bindedSample = new LayoutBuilder(ExampleLayout.layout)
		.with(sampleData)
		.bind(sample)
		assert sample == bindedSample: "The sample and the binded sample should be the same"
		checkSample(sample)
	}

	void testObjectWithLayoutBind() {
		def sample = new ExampleLayout()
		def bindedSample = new LayoutBuilder()
		.with(sampleData)
		.bind(sample)
		assert sample == bindedSample: "The sample and the binded sample should be the same"
		checkSample(sample)
	}

	def testInvalidRange() {
	}

	def testInvalidConverter() {
	}

	/**
	 * Reading a Negs formated file, which contains information about stock buying/selling
	 */
	void testBigNegsuilder() {
		def layouts = [
			header: {
				nomeArquivo 2..9
				origem range: 10..17, trim: true
				codigoCorretora range: 18..21, converter: 'Integer'
				dataArquivo range: 22..29, format: "yyyyMMdd"
				dataPregao range: 30..37, format: "yyyyMMdd"
			},
			negocio: {
				numero range: 2..8, converter: 'Integer'
				side range: 9..9
				codigoNegociacao range: 10..21, trim: true
				tipoMercado 22..24
				tipoTransacao 25..27
				nomeEmissora 28..39
				especificacao 40..49
				quantidade range: 50..60, converter: 'Integer'
				preco range: 61..71, converter: { new BigDecimal(it[0..8] + "." + it[9..10]) }
				codigoUsuarioContraparte range: 72..76, converter: 'Integer'
				tipoLiquidacao 80..80
				horaMinutoNegocio 81..85
				situacaoNegocio 86..86
				codigoObjeto 87..98
				codigoCliente range: 99..105, converter: 'Integer'
				digitoCliente range: 106..106, converter: 'Integer'
				isin 107..118
				distribuicaoIsin range: 119..121, converter: 'Integer'
			},
			footer: {
				nomeArquivo 2..9
				origem range: 10..17, trim: true
				codigoCorretora range: 18..21, converter: 'Integer'
				dataArquivo range: 22..29, format: "yyyyMMdd"
				quantidade range: 30..38, converter: 'Integer'
			}
		]
		def rows = [
			"00NEGS0173ROCANIS 01732011060120110601",
			"010000060VCRUZ3       VISNORCRUZ3       ON      NM000000148000000000208900113    10:00             00000000BRXXXXZZZZZ1000000000100000000000         00000             11003                          1 N",
			"99NEGS0173ROCANIS 017320110601000000003"
		]
		def builder = new LayoutBuilder()
		def header = builder.using(layouts.header).with(rows[0]).build()

		assert [
			header.nomeArquivo,
			header.origem,
			header.codigoCorretora,
			header.dataArquivo,
			header.dataPregao
		] == [
			"NEGS0173",
			"ROCANIS",
			173,
			new Date("2011/06/01"),
			new Date("2011/06/01")
		]
		def negocio = builder.using(layouts.negocio).with(rows[1]).build()
		assert [
			negocio.numero,
			negocio.side,
			negocio.codigoNegociacao,
			negocio.tipoMercado,
			negocio.tipoTransacao,
			negocio.especificacao,
			negocio.quantidade,
			negocio.preco,
			negocio.codigoUsuarioContraparte,
			negocio.horaMinutoNegocio,
			negocio.codigoCliente,
			negocio.digitoCliente,
			negocio.isin,
			negocio.distribuicaoIsin
		] == [
			60,
			"V",
			"CRUZ3",
			"VIS",
			"NOR",
			"ON      NM",
			14800,
			20.89,
			113,
			"10:00",
			0,
			0,
			"BRXXXXZZZZZ1",
			0
		]
		def footer = builder.using(layouts.footer).with(rows[2]).build()
		assert [
			footer.nomeArquivo,
			footer.origem,
			footer.codigoCorretora,
			footer.dataArquivo,
			footer.quantidade
		] == [
			"NEGS0173",
			"ROCANIS",
			173,
			new Date("2011/06/01"),
			3
		]
	}
}
