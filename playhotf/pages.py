#!/usr/bin/env python
'''
Encoder will convert a Model to a JSON String.
'''

import logging

import webapp2
from webapp2_extras.appengine.users import login_required
from actions import NoUserNameException, get_current_state


PAGE_HEADER = '''
<!DOCTYPE html>
<!--[if IE 8]>                  <html class="no-js lt-ie9" lang="en" > <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en" > <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">
    <title>Heroes of The Fall</title>
    <link rel="stylesheet" href="css/foundation.css">
    <script type='text/javascript' src="js/vendor/custom.modernizr.js"></script>
    <script type='text/javascript' src='js/service.js'></script>
</head>
<body>
'''

PAGE_FOOTER = '''
    <script>
    document.write('<script src=' +
    ('__proto__' in {} ? 'js/vendor/zepto' : 'js/vendor/jquery') +
    '.js><\/script>')
    </script>
    <script src="js/foundation.min.js"></script>
    <script src="js/foundation/foundation.js"></script>
    <script src="js/foundation/foundation.alerts.js"></script>
    <script src="js/foundation/foundation.clearing.js"></script>
    <script src="js/foundation/foundation.cookie.js"></script>
    <script src="js/foundation/foundation.dropdown.js"></script>
    <script src="js/foundation/foundation.forms.js"></script>
    <script src="js/foundation/foundation.joyride.js"></script>
    <script src="js/foundation/foundation.magellan.js"></script>  
    <script src="js/foundation/foundation.orbit.js"></script>
    <script src="js/foundation/foundation.reveal.js"></script>
    <script src="js/foundation/foundation.section.js"></script>
    <script src="js/foundation/foundation.tooltips.js"></script>
    <script src="js/foundation/foundation.topbar.js"></script>
    <script src="js/foundation/foundation.interchange.js"></script>
    <script src="js/foundation/foundation.placeholder.js"></script>  
    <script src="js/foundation/foundation.abide.js"></script>
    <script>
        $(document).foundation();
    </script>
'''

REGISTER_PAGE = '''
<style>
    h1, h2 {
        text-align: center;
    }
</style>
<div class="row">
    <div class="large-12 columns">
        <h1>HEROES OF THE FALL</h1>
    </div>
</div>
<div class="row">
    <div class="large-12 columns">
        <p>A play-by-post roleplay gaming web site built around a gaming system that expresses combat damage as real injuries 
        that have to be role played instead of the traditional hit-point attrition. 
        </p><p>To play, read the terms and conditions below, 
        accept them and then register a name.
        </p>
        <hr />
    </div>
</div>
<div class="row">
    <div class="large-12 columns">
        <h2>Terms and Conditions of Service</h2>
        <p>www.heroesofthefall.com and its sub-domains (referred to herein as "the site")
        are owned by the author and are operated by him and by the site administrators 
        (who are collectively referred to herein as "we", "us", or "our") 
        solely for the purpose of allowing the user 
        (referred to herein as "you" or "your")
        to create and play role-play games.
        </p><p>
        By using the site, you accept these Terms of Service.
        We reserve the right, without notification, to modify these Terms at any time.
        </p><p>
        Abuse, baiting, trolling, or personal insults of any nature are not tolerated on this site. 
        If you invite other users to play a role in in a game then you are required to take all reasonable measures to protect  
        those other users from any form of abuse while playing your game and to take action against anyone who has abused other players. You must
        remove a character from the control of any other user who you believe has behaved abusively in your game. 
        The site administrators will provide an email address (abuse@heroesofthefall.com) which anyone can use to report abuse. 
        If a complaint is made against you the site administrators may choose to take sanctions against you if they agree that you have behaved in an abusive fashion towards another user. 
        The determination of what constitutes abusive behaviour is at the sole discretion of the administrators of this site and is not open to debate. 
        You absolve the site owner and administrators from any responsibility for, any injury or harm you may suffer while using the site. 
        </p><p>
        Some areas within this site contain adult content.
        By using this site you absolve the site owner and administrators from any responsibility for preventing you or anyone else from viewing adult content if you or they are below the legal age that they are required to be in order to view such material. 
        The responsibility for ensuring that adult content is clearly labelled as such rests entirely with the user who has published that content. 
        Responsibility for ensuring that that a person viewing adult content is of the legal age that they are required to be in order to view such material 
        in accordance with the laws in their permanent place of residence and in the place in which they are currently residing  
        rests entirely with that person, except where that content has not been clearly labelled in which case the user who has published that content must accept complete responsibility.
        </p><p>
        No content from the site may be copied, reproduced, republished, uploaded, posted, transmitted, or distributed in any way without the express, written permission of the author (whether it be us or the posting author).
        Unless you are the original author you may view, download or print any site content however that content cannot be modified or altered in any way. 
        You may not republish, distribute, prepare derivative works from, or otherwise use site content other than as explicitly permitted herein.
        All content published to the site remains the copyright material of the original author, as per copyright laws.  If the poster is not the original author then applicable copyright law must be obeyed.
        Use of this site does not grant you any ownership rights to any site content.
        </p><p>
        Any infringement upon these conditions of use may lead to your access being revoked. GMs can administer their own games as they like and have the ability to ban users at their own discretion.
        </p>
    </div>
</div>
<div class="row">
    <div class="large-3 large-offset-4 large-centered columns">
        <div class="switch large round">
            <input id="accepted" name="switch-accepted" type="radio" checked
                onclick="$('#registerButton').addClass('disabled');$('#usernameField').attr('disabled','').blur();">
            <label for="accepted">Declined</label>
            <input id="accepted1" name="switch-accepted" type="radio"
                onclick="$('#registerButton').removeClass('disabled');$('#usernameField').removeAttr('disabled').focus();">
            <label for="accepted1">Accepted</label>
            <span></span>
        </div>
     </div>
    <hr/>
</div>
<div class="row">
    <div class="large-8 large-offset-2 panel radius">
        <div class="row">
            <div class="large-12 columns">
                <h2>Register Your Name</h2>
                <p style="text-align:center;">You have to create a unique name that other site users will know you by.</p> 
                <p style="text-align:center;">Choose wisely because this name cannot be changed!</p>
            </div>
        </div>
        <div class="row">
            <div class="large-6 large-offset-2 large-centered columns">
                <input id="usernameField" disabled type="text">
                <small id="usernameError"></small>
        </div>
        <div class="row">
            <div class="large-6 large-centered columns">
                <div class="row">
                    <div class="large-4 large-centered columns">
                        <script>
                            function registration() {
                                var data = new Object();
                                data.user_name = $("#usernameField").val();
                                new Service({
                                    saveData: "/services/registration",
                                    getData: function(data) {
                                        window.location = data.next_page;
                                    },
                                    messageScope: "#usernameError"
                                }).saveData(data);
                            }
                        </script>
                        <a id="registerButton" class="small round button disabled" onclick="registration();">Register</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
'''

class Register(webapp2.RequestHandler):
    @login_required
    def get(self):
        try:
            self.response.out.write(PAGE_HEADER + REGISTER_PAGE + PAGE_FOOTER)
        except Exception as e:
            handleError(self, e)

PLAY_PAGE = '''
    <h1>HEROES OF THE FALL</h1>
    <div id="messageField"></div>
    <p>User: {username}</p>
    <p>Playing: {role}</p>
    <p>in: {place}</p>
'''
class Play(webapp2.RequestHandler):
    @login_required
    def get(self):
        try:
            user_name, role, place = get_current_state()
            role_name = role.name if role != None else "No role"
            place_name = place.name if place != None else "No place"
            self.response.out.write(PAGE_HEADER + PLAY_PAGE.format(username=user_name, role=role_name, place=place_name) + PAGE_FOOTER)
        except Exception as e:
            handleError(self, e)

''' Map WSGI handlers
'''
app = webapp2.WSGIApplication([('/', Play),
                               ('/register', Register)],
                              debug=True)

def handleError(self, e):
    ''' handle errors by returning JSON either redirect the browser or display the error.
    '''
    self.response.set_status(500)
    if isinstance(e, NoUserNameException):
        self.redirect('/register')
    else:
        text = e.args
        logging.exception(e)
        self.response.write('{"level":"error", "text": "' + str(text[0]) + '"}')
