db.getSiblingDB("admin").createUser( { user: "USER",
									pwd: "PASS",
									roles: [ "root" ] } )
