package br.com.pr0g.flatfile

class LayoutBuilder {
	
	LayoutHandler handler
	
	def data
	
	def LayoutBuilder() { }
	
	def LayoutBuilder(Closure layout) {
		handler = new LayoutHandler(layout)
	}
	
	def using(Closure layout) {
		handler = new LayoutHandler(layout)
		this
	}
	
	def with(data) {
		this.data = data
		this
	}
	
	def build(Class cls) {
		def obj = cls.newInstance()
		extractHandler(obj)
		handler.bind(obj, data)
	}
	
	def extractHandler(obj) {
		if(obj.hasProperty("layout") && obj.layout instanceof Closure) {
			handler = new LayoutHandler(obj.layout)
		}
	}
	
	def bind(obj) {
		if(!handler && !extractHandler(obj)) {
			throw new IllegalStateException("Invalid layout handler, you need to supply a layout to bind the data to the object")
		}
		handler.bind(obj, data)
	}
	
	def build() {
		if(!handler) {
			throw new IllegalStateException("Invalid layout handler, you need to supply a layout to bind the data to the object")
		}
		handler.bind(new Expando(), data)
	}
	

	
	def createInstance() {
		if(!clazz) {
			throw new IllegalStateException("Invalid class, you need to supply a class to build an object instance or the object itself")
		}
		clazz.newInstance()
	}


}