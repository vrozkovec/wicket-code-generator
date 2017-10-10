# wicket-code-generator

Small utility class to generate java and html templates from given POJO classes.

Utility creates boilerplate files at given path.

Just set proper path in the *Generator.java* and run the Application to see the result.

Please note that generated files will contain compilation errors as they reference some classes that are not part of this utility.

To create your own template that suits your coding style create a set of list/view/edit components and convert them to the .tmpl files as you can see in the *name.berries.wicket.reflection.templates.bootstrap.horizontal* package.

