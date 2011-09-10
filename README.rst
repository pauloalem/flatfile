==========
 FlatFile
==========

It's still pretty common, at least in my experience, services exchanging data using arbitrary formats with fixed size strings.
FlatFile is built with Groovy and aims to ease the boring task of mapping these files to an object via a pre-defined layout.
Using FlatFile you can define the structure of a given file and easily transform the raw contents of a line in an object of yout choice.

Installation
============

Just clone this repo and build it with gradle. If you already have it installed you just need to run 

``$ gradle build``

If you dont, go to `gradle <http://gradle.org>`_ and see how to do it in your platform
After running the tests will be run and, hopefuly, you'll get a the build/libs/flatfile.jar in the projects dir.

Examples
========

Suppose you have a file with the following content::

    0000100000042250JOHN DOE        
    0000200000024250JOHN FOO        
    0000300000025200JOHN BAR        

Three lines and kinda messy, but you already know what it's composed of, you know its layout. 
Breaking it down you have:

# id, from 0 to 5:
    000010

# person's account balance, from 6 to 15:
    0004225000

# and the name, from 16 to 31:
    JOHN DOE        

So maybe you're using Grails and liked the idea of using field types to define domain classes and map them to the database(I did)::

    class Person { 
        Long id
        BigDecimal balance
        String name
        
        static constraints = {
            name unique: true 
        }

        String toString() { 
            name
        }
    }

That's enough for the database part, so now you just need to read the data and store it.
Using FlatFile you can define how that class maps to the layout ::

    class Person { 
        Long id
        BigDecimal balance
        String name
        
        static constraints = {
            name unique: true 
        }
       
        String toString() { 
            name
        }
        // =====
        static layout = { 
            id range: 0..5, converter: "Long"
            balance range: 6..15, converter: {new BigDecimal(it[0..4] + "." + it[5..9])}
            name 16..31
        }
    }

With that it becomes really easy to process that file::

    def builder = new LayoutBuilder() 
    file.eachLine { row ->
        def person = builder.with(row).build(Person)
        person.save()
    }
    Person.list()

Aaaand that's it :-)

TODO
====
Show more examples
# Other ways to build objects, using expandos or maps
# How to build converters
# Talk about filters


:Author: Paulo Alem(@biggahed) <biggahed@gmail.com>
