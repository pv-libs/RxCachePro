package com.pv_libs.cachepro_rxjava.utils

class NoConnectionError : Exception("Not connected to any network")

class ConnectedButNoInternet : Exception("Connected but no internet access")