package com.jaredsburrows.license

import spock.lang.Issue

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static test.TestUtils.assertHtml
import static test.TestUtils.assertJson
import static test.TestUtils.getLicenseText
import static test.TestUtils.gradleWithCommand

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

final class LicensePluginJavaSpec extends Specification {
  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder()
  private String mavenRepoUrl
  private File buildFile
  private String reportFolder

  def 'setup'() {
    mavenRepoUrl = getClass().getResource('/maven').toURI()
    buildFile = testProjectDir.newFile('build.gradle')
    // In case we're on Windows, fix the \s in the string containing the name
    reportFolder = "${testProjectDir.root.path.replaceAll("\\\\", '/')}/build/reports/licenses"
  }

  def 'licenseReport with no dependencies'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>None</h3>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      []
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport with no open source dependencies'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'com.google.firebase:firebase-core:10.0.1'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#0">firebase-core (10.0.1)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <a name="0"></a>
            <pre>No license found</pre>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"firebase-core",
          "description":null,
          "version":"10.0.1",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[],
          "dependency":"com.google.firebase:firebase-core:10.0.1"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport with duplicate dependencies'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'com.android.support:appcompat-v7:26.1.0'
        implementation 'com.android.support:appcompat-v7:26.1.0'
        implementation 'com.android.support:design:26.1.0'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#1934118923">appcompat-v7 (26.1.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <li>
              <a href="#1934118923">design (26.1.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <a name="1934118923"></a>
            <pre>${getLicenseText('apache-2.0.txt')}</pre>
            <br>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"appcompat-v7",
          "description":null,
          "version":"26.1.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"The Apache Software License",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.android.support:appcompat-v7:26.1.0"
        },
        {
          "project":"design",
          "description":null,
          "version":"26.1.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"The Apache Software License",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.android.support:design:26.1.0"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport with dependency with full pom with project name, developers, url, year, bad license'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'group:name3:1.0.0'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>None</h3>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      []
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport with dependency with full pom and project name, developers, url, year, single license'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'group:name:1.0.0'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#-296292112">Fake dependency name (1.0.0)</a>
              <dl>
                <dt>Copyright &copy; 2017 name</dt>
              </dl>
            </li>
            <a name="-296292112"></a>
            <pre>Some license
              <a href="http://website.tld/">http://website.tld/</a>
            </pre>
            <br>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"Fake dependency name",
          "description":"Fake dependency description",
          "version":"1.0.0",
          "developers":[
            "name"
          ],
          "url":"https://github.com/user/repo",
          "year":"2017",
          "licenses":[
            {
              "license":"Some license",
              "license_url":"http://website.tld/"
            }
          ],
          "dependency":"group:name:1.0.0"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport dependency with full pom - project name, multiple developers, url, year, multiple licenses'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'group:name2:1.0.0'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#1195092182">Fake dependency name (1.0.0)</a>
              <dl>
                <dt>Copyright &copy; 2017 name</dt>
              </dl>
            </li>
            <a name="1195092182"></a>
            <pre>Some license
              <a href="http://website.tld/">http://website.tld/</a>
            </pre>
            <br>
            <pre>Some license
              <a href="http://website.tld/">http://website.tld/</a>
            </pre>
            <br>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"Fake dependency name",
          "description":"Fake dependency description",
          "version":"1.0.0",
          "developers":[
            "name"
          ],
          "url":"https://github.com/user/repo",
          "year":"2017",
          "licenses":[
            {
              "license":"Some license",
              "license_url":"http://website.tld/"
            },
            {
              "license":"Some license",
              "license_url":"http://website.tld/"
            }
          ],
          "dependency":"group:name2:1.0.0"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport with dependency without license information that in parent pom'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'group:child:1.0.0'
        implementation 'com.squareup.retrofit2:retrofit:2.3.0'
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#1934118923">Retrofit (2.3.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <a name="1934118923"></a>
            <pre>${getLicenseText('apache-2.0.txt')}</pre>
            <br>
            <hr>
            <li>
              <a href="#-296292112">Fake dependency name (1.0.0)</a>
              <dl>
                <dt>Copyright &copy; 2017 name</dt>
              </dl>
            </li>
            <a name="-296292112"></a>
            <pre>Some license
              <a href="http://website.tld/">http://website.tld/</a>
            </pre>
            <br>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"Fake dependency name",
          "description":"Fake dependency description",
          "version":"1.0.0",
          "developers":[
            "name"
          ],
          "url":"https://github.com/user/repo",
          "year":"2017",
          "licenses":[
            {
              "license":"Some license",
              "license_url":"http://website.tld/"
            }
          ],
          "dependency":"group:child:1.0.0"
        },
        {
          "project":"Retrofit",
          "description":null,
          "version":"2.3.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"Apache 2.0",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.squareup.retrofit2:retrofit:2.3.0"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport with project dependencies - multi java modules'() {
    given:
    testProjectDir.newFile('settings.gradle') <<
      """
      include 'subproject'
      """

    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      allprojects {
        repositories {
          maven {
            url '${mavenRepoUrl}'
          }
        }
      }

      dependencies {
        implementation project(':subproject')
        implementation 'com.android.support:appcompat-v7:26.1.0'
      }

      project(':subproject') {
        apply plugin: 'java-library'

        dependencies {
          implementation 'com.android.support:design:26.1.0'
        }
      }
      """
    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#1934118923">appcompat-v7 (26.1.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <li>
              <a href="#1934118923">design (26.1.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <a name="1934118923"></a>
            <pre>${getLicenseText('apache-2.0.txt')}</pre>
            <br>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"appcompat-v7",
          "description":null,
          "version":"26.1.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"The Apache Software License",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.android.support:appcompat-v7:26.1.0"
        },
        {
          "project":"design",
          "description":null,
          "version":"26.1.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"The Apache Software License",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.android.support:design:26.1.0"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  def 'licenseReport using api and implementation configurations with multi java modules'() {
    given:
    testProjectDir.newFile('settings.gradle') <<
      """
      include 'subproject'
      """

    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      allprojects {
        repositories {
          maven {
            url '${mavenRepoUrl}'
          }
        }
      }

      dependencies {
        api project(':subproject')
        implementation 'com.android.support:appcompat-v7:26.1.0'
      }

      project(':subproject') {
        apply plugin: 'java-library'

        dependencies {
          implementation 'com.android.support:design:26.1.0'
        }
      }
      """
    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')
    def actualCsv = new File(reportFolder, 'licenseReport.csv')
    def actualHtml = new File(reportFolder, 'licenseReport.html')
    def expectedHtml =
      """
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta http-equiv="content-type" content="text/html; charset=utf-8" />
          <style>body { font-family: sans-serif } pre { background-color: #eeeeee; padding: 1em; white-space: pre-wrap; word-break: break-word; display: inline-block }</style>
          <title>Open source licenses</title>
        </head>
        <body>
          <h3>Notice for packages:</h3>
          <ul>
            <li>
              <a href="#1934118923">appcompat-v7 (26.1.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <li>
              <a href="#1934118923">design (26.1.0)</a>
              <dl>
                <dt>Copyright &copy; 20xx The original author or authors</dt>
              </dl>
            </li>
            <a name="1934118923"></a>
            <pre>${getLicenseText('apache-2.0.txt')}</pre>
            <br>
            <hr>
          </ul>
        </body>
      </html>
      """
    def actualJson = new File(reportFolder, 'licenseReport.json')
    def expectedJson =
      """
      [
        {
          "project":"appcompat-v7",
          "description":null,
          "version":"26.1.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"The Apache Software License",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.android.support:appcompat-v7:26.1.0"
        },
        {
          "project":"design",
          "description":null,
          "version":"26.1.0",
          "developers":[],
          "url":null,
          "year":null,
          "licenses":[
            {
              "license":"The Apache Software License",
              "license_url":"http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
          ],
          "dependency":"com.android.support:design:26.1.0"
        }
      ]
      """
    def actualText = new File(reportFolder, 'licenseReport.txt')

    then:
    result.task(':licenseReport').outcome == SUCCESS
    result.output.find("Wrote CSV report to .*${reportFolder}/licenseReport.csv.")
    actualCsv.exists()
    result.output.find("Wrote HTML report to .*${reportFolder}/licenseReport.html.")
    actualHtml.exists()
    result.output.find("Wrote JSON report to .*${reportFolder}/licenseReport.json.")
    actualJson.exists()
    result.output.find("Wrote Text report to .*${reportFolder}/licenseReport.txt.")
    actualText.exists()
    assertHtml(expectedHtml, actualHtml.text)
    assertJson(expectedJson, actualJson.text)
  }

  @Issue("jaredsburrows/gradle-license-plugin/issues/275")
  def 'licenseReport with encoding, such as iso-8859-1 instead of UTF-8'() {
    given:
    buildFile <<
      """
      plugins {
        id 'java-library'
        id 'com.jaredsburrows.license'
      }

      repositories {
        maven {
          url '${mavenRepoUrl}'
        }
      }

      dependencies {
        implementation 'com.sun.activation:javax.activation:1.2.0' // iso-8859-1
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'licenseReport', '-s')

    then:
    result.task(':licenseReport').outcome == SUCCESS
  }

}
