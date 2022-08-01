package com.example.anifoxapi.model.responses

import org.springframework.security.core.GrantedAuthority

class SuccessfulSigninResponse(var username: String?, val authorities: Collection<GrantedAuthority>)