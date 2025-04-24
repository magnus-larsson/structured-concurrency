Feature: karate test script for structured-concurrency tests
  for help, see: https://github.com/karatelabs/karate/wiki/IDE-Support

  Background:
    * url 'http://localhost:8080'

#  Scenario: ok test
#    Given path 'composite/ok'
#    When method get
#    Then status 200
#
#  Scenario: fail test
#    Given path 'composite/fail'
#    When method get
#    Then status 200

  Scenario: slowTest
    Given path 'composite/slow'
    When method get
    Then status 200
    And print responseTime
    # And responseTime > 2000