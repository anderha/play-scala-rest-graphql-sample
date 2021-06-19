'use strict'

const fs = require('fs')
const path = require('path')
const LoadTesting = require('easygraphql-load-tester')

const todoSchema = fs.readFileSync(path.join(__dirname, './ressources/schema.graphql'), 'utf8')

const args = {
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

const customQueries = []

const easyGraphQLLoadTester = new LoadTesting(todoSchema, args)

const runQueries = easyGraphQLLoadTester.artillery({
  customQueries,
  onlyCustomQueries: false,
  queryFile: false,
  withMutations: true,
})

module.exports = {
	runQueries,
}