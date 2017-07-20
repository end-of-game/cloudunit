

# Jenkins and GitLab configurations trigger build 
This document is a step by step configuration in order to detect changes in Gitlab and trigger Jenkins build automatically. Each modication on GitLab implies automatic build.

1. Go to your cloudunit dashboard and log in
2. Click on Portal
3. Open tab for GitLab and Jenkins

![alt text](./img/Portal.jpg "Logo Title Text 1")

# 1. GitLab configuration
## Create a GitLab key

Jenkins need the access token to access to GitLab.
1. Go to Profile Settings
2. Access Tokens
![alt text](./img/Portal_GitLab_token.jpg "Logo Title Text 1")
1. add a Personnal Access Token

    `name: cloudunit`
    
 Leave `Expires at` blank .
    
 
`[important]`: don't forget to save the token ID.

![alt text](./img/Portal_Jenkins_ApiToken.jpg "Logo Title Text 1")

## Jenkinsfile configuration
1. in your project, make sure that you have Jenkins file,

![alt text](./img/Portal_GitLab_JenkinsFile.jpg "Logo Title Text 1")

2. open your Jenkinsfile,

3. add GitLab Jenkins plugin properties line named `cloudunit` . `cloudunit` is the gitlab connection name on top up of Jenkinsfile.

    `properties([gitLabConnection('cloudunit')])`

# 2. Jenkins configuration
## Requirement
 - Jenkins version 2.46.1 (Tested).

## GitLab Jenkinsfile 

Open your Jenkins tab from your Portal then log in.

![alt text](./img/Portal_Jenkins.jpg "Logo Title Text 1")
    
1. go to Jenkins management,

![alt text](./img/Portal_Jenkins_Admin.jpg "Logo Title Text 1")

2. system configuration,

3. and scroll to the Gitlab panel and configure it,

![alt text](./img/Portal_Jenkins_GitlabPanel.jpg "Logo Title Text 1")

    `1. Connection name: cloudunit`
    `2. Gitlab host URL: https://gitlab-yourcloudunitdomain`
    `3. Credentials: Jenkins`
    A window will pop , to configure creddentials go to [How to add credentials](#How to add credentials)
    `4. Add button`
    
4. validate with save button

> Go to `Identities` and make sure that you have generated credentials token.

![alt text](./img/Portal_Jenkins_credentials.jpg "Logo Title Text 1")

## How to add credentials
In the Jenkins Credentials Provider pannel.

![alt text](./img/Portal_Jenkins_Credentials.jpg "Logo Title Text 1")


`Domain: Global identity`
  
`Type: GitLab API token`
  
`Scope: Global`
  
`API token: Paste TOKEN ID HERE`
  
`ID: cloudunit`
  

