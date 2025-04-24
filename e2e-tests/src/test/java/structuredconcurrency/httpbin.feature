Feature: sample karate test script using httpbin
  for help, see: https://github.com/karatelabs/karate/wiki/IDE-Support

  Background:
    * url 'http://localhost:8080'

  Scenario: httpbin test
    Given path 'httpbin/test'
    When method get
    Then status 200
