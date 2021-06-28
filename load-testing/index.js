'use strict'

const fs = require('fs')
const path = require('path')
const LoadTesting = require('easygraphql-load-tester')

const todoSchema = fs											// read the schema file into a variable
	.readFileSync(path.join(__dirname, './ressources/schema.graphql'), 'utf8')

const args = {													// arguments to use on queries/mutations which require input
	todo: {
		id: 1,
	},
	createTodo: {
		todoToCreate: {
			title: 'Loadtest',
			description: 'created by the load testing tool'
		}
	}
}

const customQueries = [] 										// define custom graphql queries

const easyGraphQLLoadTester = new LoadTesting(todoSchema, args)	// defines the settings for the load test, explicitly the graphql schema and the arguments

const runQueries = easyGraphQLLoadTester.artillery({ 			// define a load test using artillery
  customQueries,
  onlyCustomQueries: false,										// if true it runs only custom queries
  queryFile: false, 											// creates a file with all the queries created for the load test
  withMutations: true, 											// defines if graphql mutation queries are used by the load test
})

module.exports = {
	runQueries,													// export the function runQueries to use within other modules
}