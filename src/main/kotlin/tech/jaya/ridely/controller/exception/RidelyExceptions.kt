package tech.jaya.ridely.controller.exception

class DriverUnavailable(message: String) : Exception(message)

class DriverNotFoundException(message: String) : Exception(message)

class RideNotFoundException(message: String) : Exception(message)

class RideInvalidState(message: String) : Exception(message)