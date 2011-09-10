/**
 * Copyright (c) 2011 Paulo Rafael Migueis Alem
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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
