

# Jenkins and GitLab configurations trigger build 
This document is a step by step configuration in order to activate automatique build on all behavior of GitLab. This setting is a trigger to multibranch job configuration, all modication on GitLab implies automatic build in CloudUni/Jenkins.
1. launch CloudUnit: https://cu02.cloudunit.io/#/dashboard

    `id: johndoe`
    
    `pwd: abc2015`
2. choose Portal
3. Configure GitLab and Jenkins
![alt text](./img/Portal.jpg "Logo Title Text 1")

# GitLab configuration
## Create a GitLab key
![alt text](./img/Portal_GitLab.jpg "Logo Title Text 1")

Jenkins need the access token to listen to GitLab behaviors.
1. go to Profile Settings
2. Access Tokens
![alt text](./img/Portal_GitLab_token.jpg "Logo Title Text 1")
1. Add a Personnal Access Token

    `name: cloudunit`
    
    `Expire at: xxx`
    
2. Create button
 
`xxx` : leave it blanc to set it never or give expiration date.

`[important]`: don't forget to save the token ID.

![alt text](./img/Portal_Jenkins_ApiToken.jpg "Logo Title Text 1")

## Jenkins file configuration
1. in your project, make sure that you have Jenkins file,

![alt text](./img/Portal_GitLab_JenkinsFile.jpg "Logo Title Text 1")
2. open your Jenkinsfile,
3. add GitLab Jenkins plugin properties line named `cloudunit` . `cloudunit` is the gitlab connection name,

    `properties([gitLabConnection('cloudunit')])`

# Jenkins configuration
## Requirement
 - Jenkins version 2.46.1 (Tested).

## GitLab Jenkinsfile 
![alt text](./img/Portal_Jenkins.jpg "Logo Title Text 1")

    login:
     
      `id: root`
      `pwd: nausicaa`
    
1. go to Jenkins management,

![alt text](./img/Portal_Jenkins_Admin.jpg "Logo Title Text 1")

    1. system configuration

    2. go to Gitlab panel

![alt text](./img/Portal_Jenkins_GitlabPanel.jpg "Logo Title Text 1")

    `1. Connection name: cloudunit`
    `2. Gitlab host URL: https://gitlab-cu02.cloudunit.io/`
    `3. Credentials: Jenkins`  after, go to "How to add credentials"
    `4. Add button`
    
4. Save button

Go to `Identities` and make sure that you have generated credentials token.

![alt text](./img/Portal_Jenkins_credentials.jpg "Logo Title Text 1")

## How to add credentials
In the Jenkins Credentials Provider: Jenkins,

![alt text](./img/Portal_Jenkins_Credentials.jpg "Logo Title Text 1")

1. Domain: Global identity
  
2. Domain: GitLab API token
  
3. Scope: Global
  
4. API token: Paste TOKEN ID HERE
  
5. ID: cloudunit
  
6. Description: My cloudunit GitLab/Jenkins
  
7. Add
