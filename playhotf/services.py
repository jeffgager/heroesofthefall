#!/usr/bin/env python
'''
Encoder will convert a Model to a JSON String.
'''

import json
import logging

import webapp2

from actions import register_new_user


class RegistrationService(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(str(self.request.body)) 
            user_name = data["user_name"] if "user_name" in data.keys() else "" 
            register_new_user(user_name)
            self.response.out.write(json.dumps({"next_page": "/"}))
        except Exception as e:
            handleError(self, e)

''' Map WSGI handlers
'''
app = webapp2.WSGIApplication([('/services/registration', RegistrationService)],
                              debug=True)

def handleError(self, e):
    ''' handle errors by returning JSON error message.
    '''
    self.response.set_status(500)
    text = e.args
    logging.exception(e)
    self.response.write('{"level":"error", "text": "' + str(text[0]) + '"}')
