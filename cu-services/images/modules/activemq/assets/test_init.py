__author__ = 'langoureaux-s'


import init
import os
import shutil
import unittest

class InitTestCase(unittest.TestCase):
    """Tests for `init.py`."""

    #@classmethod
    def setUp(self):
        print("Settup unit test \n")
        shutil.copytree("test/fixtures/", "test/tmp/conf/");
        os.makedirs("test/tmp/bin/linux-x86-64")
        shutil.copy2("test/fixtures/wrapper.conf", "test/tmp/bin/linux-x86-64/")
        shutil.copy2("test/fixtures/activemq", "test/tmp/bin/linux-x86-64/")
        init.ACTIVEMQ_HOME = "test/tmp";
	init.ACTIVEMQ_CONF = init.ACTIVEMQ_HOME + '/conf'


    #@classmethod
    def tearDown(self):
        print("TearDown unit test \n")
        shutil.rmtree("test/tmp")


    def test_do_setting_activemq_users(self):
        """Check the function do_setting_activemq_users"""
        init.do_setting_activemq_users("user", "password")

        file = open(init.ACTIVEMQ_HOME +'/conf/users.properties', 'r')
        self.assertRegexpMatches(file.read(), "\s+user=password\s+", "Problem when add user on users.properties")
        file.close()

    def test_do_setting_activemq_credential(self):
	""" Check the function do_setting_activemq_credential """
	init.do_setting_activemq_credential("user", "password")


	file = open(init.ACTIVEMQ_HOME +'/conf/credentials.properties', 'r')
	contend = file.read()
        file.close()

	self.assertRegexpMatches(contend, "activemq\.username=user", "Problem when add user on credentials.properties")
	self.assertRegexpMatches(contend, "activemq\.password=password", "Problem when add user on credentials.properties")



    def test_do_setting_activemq_groups(self):
        """Check the function do_setting_activemq_groups"""
        init.do_setting_activemq_groups("groups", "user1,user2")

        file = open(init.ACTIVEMQ_HOME +'/conf/groups.properties', 'r')
        self.assertRegexpMatches(file.read(), "\s+groups=user1,user2\s+", "Problem when add user to group on groups.properties");
        file.close()

    def test_do_setting_activemq_jmx_access(self):
        """Check the function do_setting_activemq_jmx_access"""
        init.do_setting_activemq_jmx_access("read", "user", "password")

        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.password', 'r')
        self.assertRegexpMatches(file.read(), "\s+user password\s+", "Problem when add jmx user");
        file.close()

        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.access', 'r')
        self.assertRegexpMatches(file.read(), "\s+user read\s+", "Problem when add jmx user to role");
        file.close()

    def test_do_setting_activemq_web_access(self):
        """Check the function do_setting_activemq_web_access"""
        init.do_setting_activemq_web_access("role", "user", "password")

        file = open(init.ACTIVEMQ_HOME +'/conf/jetty-realm.properties', 'r')
        self.assertRegexpMatches(file.read(), "\s+user: password, role\s+", "Problem when add user to web console");
        file.close()

    def test_do_setting_activemq_wrapper(self):
        """Check the function do_setting_activemq_wrapper"""
        init.do_setting_activemq_wrapper(256, 512)

        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/wrapper.conf', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "\s+wrapper.java.initmemory=256\s+", "Problem when add min memory to wrapper");
        self.assertRegexpMatches(contend, "\s+wrapper.java.maxmemory=512\s+", "Problem when add max memory to wrapper");

    def test_do_setting_activemq_log4j(self):
        """Check the function do_setting_activemq_log4j"""

        init.do_setting_activemq_log4j("FATAL")

        file = open(init.ACTIVEMQ_HOME +'/conf/log4j.properties', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "\s+log4j.rootLogger=FATAL, console, logfile\s+", "Problem when set the log level on root logger");
        self.assertRegexpMatches(contend, "\s+log4j.logger.org.apache.activemq.audit=FATAL, audit\s+", "Problem when set the log level on audit logger");


    def test_do_setting_activemq_main(self):
        """Check the function do_setting_activemq_main"""

        init.do_setting_activemq_main("myServer", 500, "5 gb", "1 gb", 30, 1000, "topic1;topic2;topic3", "queue1;queue2;queue3", "true")

        file = open(init.ACTIVEMQ_HOME +'/conf/activemq.xml', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "\s+brokerName=\"myServer\"\s+", "Problem when set the server name")
        self.assertRegexpMatches(contend, "<constantPendingMessageLimitStrategy limit=\"500\"/>", "Problem when set the message limit")
        self.assertRegexpMatches(contend, "<storeUsage limit=\"5 gb\"/>", "Problem when set the storage usage")
        self.assertRegexpMatches(contend, "<tempUsage limit=\"1 gb\"/>", "Problem when set the temp usage")
        self.assertRegexpMatches(contend, "<transportConnector .*\?maximumConnections=30.*/>", "Problem when set the max connection on broker")
        self.assertRegexpMatches(contend, "<transportConnector .*wireFormat.maxFrameSize=1000.*/>", "Problem when set the max frame size")
        self.assertRegexpMatches(contend, "<broker schedulerSupport=\"true\"", "Problem when enabled scheduler")
        self.assertRegexpMatches(contend, "<destinations>\s*<topic physicalName=\"topic1\"\s*/>\s*<topic physicalName=\"topic2\"\s*/>\s*<topic physicalName=\"topic3\"\s*/>\s*<queue physicalName=\"queue1\"\s*/>\s*<queue physicalName=\"queue2\"\s*/>\s*<queue physicalName=\"queue3\"\s*/>\s*</destinations>", "Problem with static topic and queue")

        rightManagement = """<plugins>
      		             <!--  use JAAS to authenticate using the login.config file on the classpath to configure JAAS -->
      		             <jaasAuthenticationPlugin configuration="activemq" />
		                 <authorizationPlugin>
        		            <map>
          			            <authorizationMap>
            				        <authorizationEntries>
              					        <authorizationEntry queue=">" read="admins,reads,writes,owners" write="admins,writes,owners" admin="admins,owners" />
              					        <authorizationEntry topic=">" read="admins,reads,writes,owners" write="admins,writes,owners" admin="admins,owners" />
              					        <authorizationEntry topic="ActiveMQ.Advisory.>" read="admins,reads,writes,owners" write="admins,reads,writes,owners" admin="admins,reads,writes,owners"/>
            				        </authorizationEntries>

            				        <!-- let's assign roles to temporary destinations. comment this entry if we don't want any roles assigned to temp destinations  -->
            				        <tempDestinationAuthorizationEntry>
              					        <tempDestinationAuthorizationEntry read="tempDestinationAdmins" write="tempDestinationAdmins" admin="tempDestinationAdmins"/>
           				            </tempDestinationAuthorizationEntry>
          			            </authorizationMap>
        		            </map>
      		             </authorizationPlugin>
	                     </plugins>\n"""

        self.assertRegexpMatches(contend, rightManagement, "Problem with inject right management")


    def test_do_remove_default_account(self):
        """
        Check the function do_remove_default_account
        """

        init.do_remove_default_account()

        # We check the default value on users.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/users.properties', 'r')
        contend = file.read()
        file.close()

        self.assertNotRegexpMatches(contend, "admin=admin", "Problem when remove default value on users.properties")

        # We check the default value on groups.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/groups.properties', 'r')
        contend = file.read()
        file.close()
        self.assertNotRegexpMatches(contend, "admins=admin", "Problem when remove the default value on groups.properties")

        # We check the default value on jetty-realm.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/jetty-realm.properties', 'r')
        contend = file.read()
        file.close()
        self.assertNotRegexpMatches(contend, "admin: admin, admin", "Problem when remove the default value on jetty-realm.properties")
        self.assertNotRegexpMatches(contend, "user: user, user", "Problem when remove the default value on jetty-realm.properties")

        # We check the default value on jmx.access and jmx.password
        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.access', 'r')
        contend = file.read()
        file.close()
        self.assertNotRegexpMatches(contend, "admin readwrite", "Problem when remove the default value on jmx.access")

        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.password', 'r')
        contend = file.read()
        file.close()
        self.assertNotRegexpMatches(contend, "admin activemq", "Problem when remove the default value on jmx.password")


	file = open(init.ACTIVEMQ_HOME +'/conf/credentials.properties', 'r')
        contend = file.read()
        file.close()

        self.assertNotRegexpMatches(contend, "activemq\.username=system", "Problem when remove default user on credentials.properties")
        self.assertNotRegexpMatches(contend, "activemq\.password=manager", "Problem when remove default user on credentials.properties")
	self.assertNotRegexpMatches(contend, "guest\.password=password", "Problem when remove default user on credentials.properties")



    def test_do_init_activemq(self):
        """
        Test the function do_init_activemq
        :return:
        """


        init.do_init_activemq()

        # We check the value on init script
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/activemq', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "RUN_AS_USER=activemq", "Problem when init the init script")

        # We check value on wrapper
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/wrapper.conf', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "set.default.ACTIVEMQ_DATA=/data/activemq", "Problem when init the wrapper.conf")
        self.assertRegexpMatches(contend, "wrapper.logfile=/var/log/activemq/wrapper.log", "Problem when init the wrapper.conf")
	self.assertRegexpMatches(contend, "set.default.ACTIVEMQ_CONF=%ACTIVEMQ_BASE%/conf.tmp", "Problem when init the wrapper.conf")

        # We check the value on log4j
        file = open(init.ACTIVEMQ_HOME +'/conf/log4j.properties', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "/var/log/activemq/", "Problem when init the log4j")



    def test_setting_all(self):
        """
        Check the function setting_all
        """

        # Check all default value are good
        init.setting_all()

        # We check the default value on users.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/users.properties', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "admin=admin", "Problem with default value on users.properties")

        # We check the default value on groups.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/groups.properties', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "admins=admin", "Problem with default value on groups.properties")

        # We check the default value on jetty-realm.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/jetty-realm.properties', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "admin: admin, admin", "Problem with default value on jetty-realm.properties")
        self.assertRegexpMatches(contend, "user: user, user", "Problem with default value on jetty-realm.properties")

        # We check the default value on jmx.access and jmx.password
        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.access', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "admin readwrite", "Problem with default value on jmx.access")

        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.password', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "admin activemq", "Problem with default value on jmx.password")

        # We check the default value on log4.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/log4j.properties', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "log4j\.rootLogger=INFO, console, logfile", "Problem with default value on log4j.properties")
        self.assertRegexpMatches(contend, "log4j\.logger\.org\.apache\.activemq\.audit=INFO, audit", "Problem with default value on log4j.properties")

        # We check the default value on wrapper.conf
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/wrapper.conf', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "wrapper.java.initmemory=128", "Problem with default value on wrapper.conf");
        self.assertRegexpMatches(contend, "wrapper.java.maxmemory=1024", "Problem with default value on wrapper.conf");

        # We check the default value on activemq.xml
        file = open(init.ACTIVEMQ_HOME +'/conf/activemq.xml', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "\s+brokerName=\"localhost\"\s+", "Problem with the default value on activemq.xml")
        self.assertRegexpMatches(contend, "<constantPendingMessageLimitStrategy limit=\"1000\"/>", "Problem with the default value on activemq.xml")
        self.assertRegexpMatches(contend, "<storeUsage limit=\"100 gb\"/>", "Problem with the default value on activemq.xml")
        self.assertRegexpMatches(contend, "<tempUsage limit=\"50 gb\"/>", "Problem with the default value on activemq.xml")
        self.assertRegexpMatches(contend, "<transportConnector .*\?maximumConnections=1000.*/>", "Problem with the default value on activemq.xml")
        self.assertRegexpMatches(contend, "<transportConnector .*wireFormat.maxFrameSize=104857600.*/>", "Problem with the default value on activemq.xml")
        self.assertNotRegexpMatches(contend, "<broker schedulerSupport=\"true\"", "Problem with the default value on activemq.xml")
        self.assertNotRegexpMatches(contend, "<destinations>.*</destinations>", "Problem with the default value on activemq.xml")

        rightManagement = """<plugins>
      		             <!--  use JAAS to authenticate using the login.config file on the classpath to configure JAAS -->
      		             <jaasAuthenticationPlugin configuration="activemq" />
		                 <authorizationPlugin>
        		            <map>
          			            <authorizationMap>
            				        <authorizationEntries>
              					        <authorizationEntry queue=">" read="admins,reads,writes,owners" write="admins,writes,owners" admin="admins,owners" />
              					        <authorizationEntry topic=">" read="admins,reads,writes,owners" write="admins,writes,owners" admin="admins,owners" />
              					        <authorizationEntry topic="ActiveMQ.Advisory.>" read="admins,reads,writes,owners" write="admins,reads,writes,owners" admin="admins,reads,writes,owners"/>
            				        </authorizationEntries>

            				        <!-- let's assign roles to temporary destinations. comment this entry if we don't want any roles assigned to temp destinations  -->
            				        <tempDestinationAuthorizationEntry>
              					        <tempDestinationAuthorizationEntry read="tempDestinationAdmins" write="tempDestinationAdmins" admin="tempDestinationAdmins"/>
           				            </tempDestinationAuthorizationEntry>
          			            </authorizationMap>
        		            </map>
      		             </authorizationPlugin>
	                     </plugins>\n"""

        self.assertRegexpMatches(contend, rightManagement, "Problem with the default value on activemq.xml")

        # We check the value on init script
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/activemq', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "RUN_AS_USER=activemq", "Problem when init the init script")

        # We check value on wrapper
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/wrapper.conf', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "set.default.ACTIVEMQ_DATA=/data/activemq", "Problem when init the wrapper.conf")
        self.assertRegexpMatches(contend, "wrapper.logfile=/var/log/activemq/wrapper.log", "Problem when init the wrapper.conf")

        # We check the value on log4j
        file = open(init.ACTIVEMQ_HOME +'/conf/log4j.properties', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "/var/log/activemq/", "Problem when init the log4j")


        ########################################################################################
        # We now check all parameters
        os.environ["ACTIVEMQ_NAME"] = "myTest"
        os.environ["ACTIVEMQ_LOGLEVEL"] = "DEBUG"
        os.environ["ACTIVEMQ_PENDING_MESSAGE_LIMIT"] = "2000"
        os.environ["ACTIVEMQ_STORAGE_USAGE"] = "10 gb"
        os.environ["ACTIVEMQ_TEMP_USAGE"] = "5 gb"
        os.environ["ACTIVEMQ_MAX_CONNECTION"] = "10"
        os.environ["ACTIVEMQ_FRAME_SIZE"] = "2000000"
        os.environ["ACTIVEMQ_MIN_MEMORY"] = "256"
        os.environ["ACTIVEMQ_MAX_MEMORY"] = "512"
        os.environ["ACTIVEMQ_ADMIN_LOGIN"] = "admin"
        os.environ["ACTIVEMQ_ADMIN_PASSWORD"] = "P@ssw0rd"
        os.environ["ACTIVEMQ_USER_LOGIN"] = "disaster"
        os.environ["ACTIVEMQ_USER_PASSWORD"] = "pasword1234"
        os.environ["ACTIVEMQ_READ_LOGIN"] = "read"
        os.environ["ACTIVEMQ_READ_PASSWORD"] = "read1234"
        os.environ["ACTIVEMQ_WRITE_LOGIN"] = "write"
        os.environ["ACTIVEMQ_WRITE_PASSWORD"] = "write1234"
        os.environ["ACTIVEMQ_OWNER_LOGIN"] = "owner"
        os.environ["ACTIVEMQ_OWNER_PASSWORD"] = "owner1234"
        os.environ["ACTIVEMQ_JMX_LOGIN"] = "jmx"
        os.environ["ACTIVEMQ_JMX_PASSWORD"] = "jmx1234"
        os.environ["ACTIVEMQ_STATIC_TOPICS"] = "topic1;topic2"
        os.environ["ACTIVEMQ_STATIC_QUEUES"] = "queue1;queue2;queue3"
        os.environ["ACTIVEMQ_REMOVE_DEFAULT_ACCOUNT"] = "true"
	os.environ["ACTIVEMQ_ENABLED_SCHEDULER"] = "true"



        init.setting_all()

        # We check the default value on users.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/users.properties', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "admin=P@ssw0rd", "Problem with set value on users.properties")
        self.assertRegexpMatches(contend, "read=read1234", "Problem with set value on users.properties")
        self.assertRegexpMatches(contend, "write=write1234", "Problem with set value on users.properties")
        self.assertRegexpMatches(contend, "owner=owner1234", "Problem with set value on users.properties")

        # We check the default value on groups.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/groups.properties', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "admins=admin", "Problem with set value on groups.properties")
        self.assertRegexpMatches(contend, "reads=read", "Problem with set value on groups.properties")
        self.assertRegexpMatches(contend, "writes=write", "Problem with set value on groups.properties")
        self.assertRegexpMatches(contend, "owners=owner", "Problem with set value on groups.properties")

        # We check the default value on jetty-realm.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/jetty-realm.properties', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "admin: P@ssw0rd, admin", "Problem with default value on jetty-realm.properties")
        self.assertRegexpMatches(contend, "disaster: pasword1234, user", "Problem with default value on jetty-realm.properties")

        # We check the default value on jmx.access and jmx.password
        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.access', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "jmx readwrite", "Problem with set value on jmx.access")

        file = open(init.ACTIVEMQ_HOME +'/conf/jmx.password', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "jmx jmx1234", "Problem with set value on jmx.password")

        # We check the default value on log4.properties
        file = open(init.ACTIVEMQ_HOME +'/conf/log4j.properties', 'r')
        contend = file.read()
        file.close()
        self.assertRegexpMatches(contend, "log4j\.rootLogger=DEBUG, console, logfile", "Problem with set value on log4j.properties")
        self.assertRegexpMatches(contend, "log4j\.logger\.org\.apache\.activemq\.audit=DEBUG, audit", "Problem with set value on log4j.properties")

        # We check the default value on wrapper.conf
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/wrapper.conf', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "wrapper.java.initmemory=256", "Problem with set value on wrapper.conf");
        self.assertRegexpMatches(contend, "wrapper.java.maxmemory=512", "Problem with set value on wrapper.conf");

        # We check the default value on activemq.xml
        file = open(init.ACTIVEMQ_HOME +'/conf/activemq.xml', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "\s+brokerName=\"myTest\"\s+", "Problem with set the  value on activemq.xml")
        self.assertRegexpMatches(contend, "<constantPendingMessageLimitStrategy limit=\"2000\"/>", "Problem with set the value on activemq.xml")
        self.assertRegexpMatches(contend, "<storeUsage limit=\"10 gb\"/>", "Problem with set the value on activemq.xml")
        self.assertRegexpMatches(contend, "<tempUsage limit=\"5 gb\"/>", "Problem with set the value on activemq.xml")
        self.assertRegexpMatches(contend, "<transportConnector .*\?maximumConnections=10.*/>", "Problem with set the value on activemq.xml")
        self.assertRegexpMatches(contend, "<transportConnector .*wireFormat.maxFrameSize=2000000.*/>", "Problem with set the value on activemq.xml")
	self.assertRegexpMatches(contend, "<broker schedulerSupport=\"true\"", "Problem with set the value on activemq.xml")
        self.assertRegexpMatches(contend, "<destinations>\s*<topic physicalName=\"topic1\"\s*/>\s*<topic physicalName=\"topic2\"\s*/>\s*<queue physicalName=\"queue1\"\s*/>\s*<queue physicalName=\"queue2\"\s*/>\s*<queue physicalName=\"queue3\"\s*/>\s*</destinations>", "Problem with set the value on activemq.xml")

        rightManagement = """<plugins>
      		             <!--  use JAAS to authenticate using the login.config file on the classpath to configure JAAS -->
      		             <jaasAuthenticationPlugin configuration="activemq" />
		                 <authorizationPlugin>
        		            <map>
          			            <authorizationMap>
            				        <authorizationEntries>
              					        <authorizationEntry queue=">" read="admins,reads,writes,owners" write="admins,writes,owners" admin="admins,owners" />
              					        <authorizationEntry topic=">" read="admins,reads,writes,owners" write="admins,writes,owners" admin="admins,owners" />
              					        <authorizationEntry topic="ActiveMQ.Advisory.>" read="admins,reads,writes,owners" write="admins,reads,writes,owners" admin="admins,reads,writes,owners"/>
            				        </authorizationEntries>

            				        <!-- let's assign roles to temporary destinations. comment this entry if we don't want any roles assigned to temp destinations  -->
            				        <tempDestinationAuthorizationEntry>
              					        <tempDestinationAuthorizationEntry read="tempDestinationAdmins" write="tempDestinationAdmins" admin="tempDestinationAdmins"/>
           				            </tempDestinationAuthorizationEntry>
          			            </authorizationMap>
        		            </map>
      		             </authorizationPlugin>
	                     </plugins>\n"""

        self.assertRegexpMatches(contend, rightManagement, "Problem with set the value on activemq.xml")



        # We check the value on init script
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/activemq', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "RUN_AS_USER=activemq", "Problem when init the init script")

        # We check value on wrapper
        file = open(init.ACTIVEMQ_HOME +'/bin/linux-x86-64/wrapper.conf', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "set.default.ACTIVEMQ_DATA=/data/activemq", "Problem when init the wrapper.conf")
        self.assertRegexpMatches(contend, "wrapper.logfile=/var/log/activemq/wrapper.log", "Problem when init the wrapper.conf")

        # We check the value on log4j
        file = open(init.ACTIVEMQ_HOME +'/conf/log4j.properties', 'r')
        contend = file.read()
        file.close()

        self.assertRegexpMatches(contend, "/var/log/activemq/", "Problem when init the log4j")



if __name__ == '__main__':
    unittest.main()
