/**
 * Copyright (c) 2011 Paulo Rafael Migueis Alem
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package br.com.pr0g.flatfile

import java.util.Map;

class LayoutHandler {
	
	Closure layout
	
	def instance
	
	def data
	
	static final CONVERTERS = [
		'BigDecimal': { new BigDecimal(it[0..10] + "." + it[11..17]) },
		'Integer': { Integer.valueOf(it) },
		'Long': { Long.valueOf(it) },
	]
	
	static final FILTERS = [
		'trim': { it.trim() },
		'lower': { it.toLowerCase() },
		'upper': { it.toUpperCase() },
	]
	
	def LayoutHandler(Closure layout) {
		layout.delegate = this
		layout.resolveStrategy = Closure.DELEGATE_FIRST
		this.layout = layout
	}
	
	def bind(instance, data) {
		this.instance = instance
		this.data = data
		layout()
		this.instance
	}
	
	def propertyMissing(String name, arg) {
		instance.metaClass[name] = arg
	}

	def methodMissing(String name, args) {
		if(isSimpleArgumentCall(args)) {
			def clazz = getFieldType(name)
			if(clazz) {
				// if typed, can create an instance of it
				attr(name, clazz.newInstance(data[args[0]]))
			} else {
				// Its probably a simple string. If its not, the user should pass the appropriate converter.
				attr(name, data[args[0]])
			}
		} else {
			//complex argument, requires some extra parsing through convert()
			attr(name,convert(name, args[0]))
		}
	}

	def attr(name, value) {
		instance[name] = value
	}

	def convert(String name, Map args) {
		
		def val = args.trim? data[args.range].trim(): data[args.range]

		def type = getFieldType(name)
		if(type?.name == 'java.util.Date' || args.format) {
			if(!args.format && !args.converter) {
				throw new Exception("Ranges de data obrigatoriamente precisam passar um parametro format especificando o pattern da data (Ex: dd/MM/yyyy)")
			}
			def formatter = new java.text.SimpleDateFormat(args.format)
			return formatter.parse(val)
		}

		if(args.converter) {
			def conv = args.converter
			try {
				if(conv instanceof Closure) {
					return conv(val)
				} else if(conv instanceof String) {
					return CONVERTERS[conv](val)
				}
			} catch(ex) {
				throw new Exception("Error applying ($name) converter on ($val)")
			}
		} else if(getFieldType(name)) {
			return getFieldType(name).newInstance(val)
		} else {
			return val
		}
	}
	
	def getFieldType(name) {
		instance.getMetaClass().properties.find{ it.name == name }?.type
	}
	
	/**
	 * A simple argument call is a call to build a attribute which only has a range as a parameter. 
	 * It's the simplest way to define a column. The range defined will be extracted from the source data and 
	 * assigned to the object being built.
	 * @param args The arguments that define the mapped column 
	 * @return boolean
	 */
	def isSimpleArgumentCall(args) {
		args[0] instanceof IntRange
	}
}
